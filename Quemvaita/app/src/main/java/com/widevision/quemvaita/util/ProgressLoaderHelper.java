package com.widevision.quemvaita.util;

/**
 * Created by mercury-five on 20/01/16.
 */
public class ProgressLoaderHelper {

    private static volatile ProgressLoaderHelper instance = null;

  //  private SweetAlertDialog dialog;

    // private constructor
    private ProgressLoaderHelper() {
    }

    public static ProgressLoaderHelper getInstance() {
        if (instance == null) {
            synchronized (ProgressLoaderHelper.class) {
                // Double check
                if (instance == null) {
                    instance = new ProgressLoaderHelper();

                }
            }
        }
        return instance;
    }

   /* public void showProgress(Activity activity) {
            dialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
            dialog.setTitleText("Loading...");
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null) {
            dialog.cancel();
        }
    }*/

}
