package com.sapps.www.tumblrapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Hoang on 8/26/2014.
 */
public class AddFavoriteDialogFragment extends DialogFragment {

    private String mBlogName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.add_favorite_dialog, null);

        final EditText mEditText = (EditText) v.findViewById(R.id.add_favorite_edittext);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton("Add to favorites", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mBlogName = mEditText.getText().toString();
                        if(mBlogName != null && !mBlogName.isEmpty()) {
                            BlogChecker checker = new BlogChecker(getActivity(), mBlogName);
                            checker.checkBlog();
                        }
                        hideSoftKeyboard(mEditText);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideSoftKeyboard(mEditText);
                    }
                })
                .create();
    }

    private void hideSoftKeyboard(EditText editText){
        Activity activity = getActivity();
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
