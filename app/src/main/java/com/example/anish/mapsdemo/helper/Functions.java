package com.example.anish.mapsdemo.helper;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

/**
 * Created by anish on 27-07-2017.
 */

public class Functions {
    public static void setPermission(final Context context, @NonNull String[] permissions, PermissionListener permissionListene) {

        if (permissions != null && permissions.length == 0 && permissionListene != null) {
            return;
        }
        new TedPermission(context)
                .setPermissionListener(permissionListene)
                .setDeniedMessage("If you reject permission,you can not use this service\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(permissions)
                .check();
    }
}



/*
                            //\\
                           ((***))     ___o.0___
 _/""""""""""""""""\__       ||           ||
 -@-----------------@-       ||          /  \
/////////////////////////////////////////////////////


¶▅c●▄███████||▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅||█~ ::~ :~ :►
▄██ ▲  █ █ ██▅▄▃▂
███▲ ▲ █ █ ███████
███████████████████████►
◥☼▲⊙▲⊙▲⊙▲⊙▲⊙▲⊙▲⊙☼◤

*/
