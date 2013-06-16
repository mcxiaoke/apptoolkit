package com.mcxiaoke.apptoolkit.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.fragment
 * User: mcxiaoke
 * Date: 13-6-16
 * Time: 下午6:28
 */
public class AlertDialogFragment extends BaseDialogFragment {

    public static abstract class SimpleDialogClickListener implements OnDialogClickListener {

        @Override
        public void onNegativeClick(DialogInterface dialog, int id) {
        }

        @Override
        public void onNeutralClick(DialogInterface dialog, int id) {
        }

        @Override
        public void onPositiveClick(DialogInterface dialog, int id) {
        }
    }

    public static interface OnDialogClickListener {

        /**
         * This method is invoked when the positive button is clicked
         */
        public void onPositiveClick(DialogInterface dialog, int id);

        /**
         * This method is invoked when the negative button is clicked
         */
        public void onNegativeClick(DialogInterface dialog, int id);

        /**
         * This method is invoked hen the neutral button is clicked
         */
        public void onNeutralClick(DialogInterface dialog, int id);

    }

    public static class Builder {
        public int index;
        public String title;
        public String message;
        public String positiveText;
        public String negativeText;
        public String neutralText;
        public boolean cancelable;
        public boolean canceledOnTouchOutside;
        OnDialogClickListener listener;

        public Builder setIndex(int id) {
            index = id;
            return this;
        }

        public Builder setTitle(String text) {
            title = text;
            return this;
        }

        public Builder setMessage(String text) {
            message = text;
            return this;
        }

        public Builder setPositiveButton(String text) {
            positiveText = text;
            return this;
        }

        public Builder setNegativeButton(String text) {
            negativeText = text;
            return this;
        }

        public Builder setNeutralButton(String text) {
            neutralText = text;
            return this;
        }

        public Builder setCancelable(boolean value) {
            cancelable = value;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean value) {
            canceledOnTouchOutside = value;
            return this;
        }

        public Builder setClickListener(OnDialogClickListener li) {
            listener = li;
            return this;
        }

        public AlertDialogFragment build() {
            AlertDialogFragment fragment = new AlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", index);
            args.putString("positive", positiveText);
            args.putString("negative", negativeText);
            args.putString("neutral", neutralText);
            args.putString("title", title);
            args.putString("message", message);
            args.putBoolean("cancelable", cancelable);
            args.putBoolean("canceledOnTouchOutside", canceledOnTouchOutside);
            fragment.setArguments(args);
            fragment.setClickListener(listener);
            return fragment;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private OnDialogClickListener mListener;
    private int mIndex;

    public void setClickListener(OnDialogClickListener li) {
        mListener = li;
    }

    @Override
    public Dialog onCreateDialog(Bundle sis) {
        Bundle bundle = getArguments();
        mIndex = bundle.getInt("id");
        String posText = bundle.getString("positive");
        String negText = bundle.getString("negative");
        String neuText = bundle.getString("neutral");
        String title = bundle.getString("title");
        String message = bundle.getString("message");
        boolean cancelable = bundle.getBoolean("cancelable", true);
        boolean canceledOnTouchOutside = bundle.getBoolean("canceledOnTouchOutside", false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (posText != null) {
            builder.setPositiveButton(posText, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    if (mListener != null) {
                        mListener.onPositiveClick(dialog, mIndex);
                    }
                }
            });
        }
        if (negText != null) {
            builder.setNegativeButton(negText, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    if (mListener != null) {
                        mListener.onNegativeClick(dialog, mIndex);
                    }
                }
            });
        }
        if (neuText != null) {
            builder.setNeutralButton(neuText, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    if (mListener != null) {
                        mListener.onNeutralClick(dialog, mIndex);
                    }
                }
            });
        }
        builder.setMessage(message);
        builder.setTitle(title);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        return dialog;
    }
}
