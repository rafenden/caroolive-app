/*
 * Copyright (c) 2014. Pokevian Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pokevian.app.smartfleet.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.pokevian.app.smartfleet.ui.BaseActivity;
import com.pokevian.app.smartfleet.ui.common.GoogleAuthFragment.GoogleAuthCallbacks;

import org.apache.log4j.Logger;

public class GoogleAuthActivity extends BaseActivity implements GoogleAuthCallbacks {

    static final String TAG = "GoogleAuthActivity";
    final Logger logger = Logger.getLogger(TAG);

    public static final String EXTRA_LOGIN_ID = "extra.LOGIN_ID";
    public static final String EXTRA_ACCOUNT_IMAGE_URL = "extra.ACCOUNT_IMAGE_URL";

    private String mLoginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Intent data = getIntent();
            mLoginId = data.getStringExtra(EXTRA_LOGIN_ID);

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = GoogleAuthFragment.newInstance(mLoginId);
            fm.beginTransaction().add(fragment, GoogleAuthFragment.TAG)
                    .commitAllowingStateLoss();
        } else {
            mLoginId = savedInstanceState.getString(EXTRA_LOGIN_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_LOGIN_ID, mLoginId);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GoogleAuthFragment.REQUEST_GOOGLE_AUTH) {
            // Pass REQUEST_GOOGLE_AUTH through
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag(GoogleAuthFragment.TAG);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onGoogleAuthSuccess(GoogleAuthFragment fragment, String loginId, String imageUrl) {
        logger.debug("onGoogleAuthSuccess(): loginId=" + loginId);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().remove(fragment).commitAllowingStateLoss();

        Intent result = new Intent();
        result.putExtra(EXTRA_LOGIN_ID, loginId);
        result.putExtra(EXTRA_ACCOUNT_IMAGE_URL, imageUrl);
        setResult(RESULT_OK, result);

        finish();
    }

    @Override
    public void onGoogleAuthFailure(GoogleAuthFragment fragment) {
        logger.warn("onGoogleAuthFailure()");

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().remove(fragment).commitAllowingStateLoss();

        finish();
    }

}
