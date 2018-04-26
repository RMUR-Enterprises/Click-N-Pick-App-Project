package com.example.app.vendor.fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.AppPreferences;
import com.example.app.R;
import com.example.app.Routes;
import com.example.app.models.MyResponse;
import com.example.app.vendor.VendorActivity;
import com.example.utils.InputDialog;
import com.example.utils.PathUtil;
import com.example.utils.api.APITask;
import com.example.utils.api.ResponseCallback;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;

import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class VendorProfileFragment extends Fragment {

    public static final String KEY_EXTRA_PROFILE = "profile";
    private static final int RC_PICK_IMAGE = 1089;

    private MyResponse profile;

    public VendorProfileFragment() {
    }

    private boolean readArgs() {
        Bundle args = getArguments();
        if (args == null) return false;
        profile = (MyResponse) args.getSerializable(KEY_EXTRA_PROFILE);
        return true;
    }

    public static VendorProfileFragment newInstance() {
        return new VendorProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_vendor_account, container, false);
        if (readArgs()) {

            final EditText shopNameField = rootView.findViewById(R.id.shopNameField);
            final EditText contactNumberField = rootView.findViewById(R.id.contactNumberField);

            shopNameField.setText(profile.name);
            contactNumberField.setText(profile.contact);

            if (profile.profilePic != null) {
                ImageView profilePicView = rootView.findViewById(R.id.profilePictureView);
                String baseUrl = Routes.getProfilePicUrl(getContext(), profile.profilePic);
                Glide.with(this).load(baseUrl).into(profilePicView);
            }

            Button updateBtn = rootView.findViewById(R.id.updateBtn);
            Button changePasswordBtn = rootView.findViewById(R.id.changePasswordBtn);
            Button changePicBtn = rootView.findViewById(R.id.changePicBtn);

            changePicBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickChangePic();
                }
            });

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String shopName = shopNameField.getText().toString().trim();
                    String contactNumber = contactNumberField.getText().toString().trim();

                    if (shopName.equals("")) {
                        shopNameField.setError("Enter shop name!");
                        return;
                    } else if (contactNumber.equals("")) {
                        contactNumberField.setError("Enter contact number!");
                        return;
                    }

                    updateProfileImpl(shopName, contactNumber);
                }
            });

            changePasswordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() == null) return;
                    InputDialog inputDialog = new InputDialog(getActivity(), new InputDialog.Callback() {
                        @Override
                        public void onDone(String string) {
                            if (string != null) {
                                if (string.length() < 4) {
                                    Toast.makeText(getActivity(), "Password must be at least 4 characters long.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    changePasswordImpl(string);
                                }
                            }
                        }
                    }).setTitle("Enter new password").setHint("password");
                    inputDialog.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputDialog.show();
                }
            });
        }
        return rootView;

    }

    private void onClickChangePic() {
        Permissions.check(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE, null,
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        try {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            photoPickerIntent.setType("image/*");
                            photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                            startActivityForResult(photoPickerIntent, RC_PICK_IMAGE);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed to pick image!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateProfileImpl(String shopName, String contactNumber) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Updating profile");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", shopName);
            jsonObject.put("location", profile.location);
            jsonObject.put("contact", contactNumber);
        } catch (JSONException ignored) {
        }

        APITask task = new APITask(jsonObject.toString(), new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);
                Toast.makeText(getContext(), myResponse.message, Toast.LENGTH_SHORT).show();
                if (response.isSuccessful() && getActivity() != null) {
                    VendorActivity activity = (VendorActivity) getActivity();
                    activity.showProfile(activity.getSupportFragmentManager());
                }
            }
        });
        task.setToken(AppPreferences.getVendorToken(getContext()))
                .execute(Routes.getUpdateVendorProfileUrl(getContext()));
    }

    private void changePasswordImpl(String password) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Changing password..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", password);
        } catch (JSONException ignored) {
        }

        APITask apiTask = new APITask(jsonObject.toString(), new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);
                Toast.makeText(getContext(), myResponse.message, Toast.LENGTH_SHORT).show();
            }
        });

        apiTask.setToken(AppPreferences.getVendorToken(getContext()))
                .execute(Routes.getChangePasswordUrl(getContext()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_PICK_IMAGE && resultCode == RESULT_OK && intent != null) {
            Uri photoUri = intent.getData();
            try {
                String path = PathUtil.getPath(getContext(), photoUri);
                uploadImageFinal(path);
            } catch (URISyntaxException e) {
                Toast.makeText(getContext(), "Unable to get path..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageFinal(String path) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading photo..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        APITask apiTask = new APITask(null, new ResponseCallback() {
            @Override
            public void onResult(Response response) {
                progressDialog.dismiss();
                if (response == null) return;
                MyResponse myResponse = MyResponse.from(response);
                Toast.makeText(getContext(), myResponse.message, Toast.LENGTH_SHORT).show();
                if (response.isSuccessful() && getActivity() != null) {
                    VendorActivity activity = (VendorActivity) getActivity();
                    activity.showProfile(activity.getSupportFragmentManager());
                }
            }
        });

        apiTask.setFileToUpload(new File(path))
                .setToken(AppPreferences.getVendorToken(getContext()))
                .execute(Routes.getUpdateProfilePicUrl(getContext()));
    }

}
