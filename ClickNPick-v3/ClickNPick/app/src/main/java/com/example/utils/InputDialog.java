package com.example.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class InputDialog {

    public interface Callback {
        void onDone(String string);
    }

    private final AlertDialog.Builder builder;
    private EditText inputEditText;

    public InputDialog(@NonNull Activity activity, @NonNull final Callback callback) {
        builder = new AlertDialog.Builder(activity);

        inputEditText = new EditText(activity);
        inputEditText.setPadding(20, 20, 20, 20);
        inputEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        builder.setView(inputEditText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string = inputEditText.getText().toString().trim();
                        callback.onDone(string);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        callback.onDone(null);
                    }
                });
    }

    public InputDialog setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public InputDialog setHint(String hint) {
        inputEditText.setHint(hint);
        return this;
    }

    public EditText getEditText() {
        return inputEditText;
    }

    public InputDialog setText(String text) {
        inputEditText.setText(text);
        return this;
    }

    public void show() {
        builder.show();
    }

}
