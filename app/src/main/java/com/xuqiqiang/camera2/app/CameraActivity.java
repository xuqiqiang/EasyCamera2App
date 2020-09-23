package com.xuqiqiang.camera2.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.snailstudio2010.camera.qrcode.QRCodeDetector;
import com.snailstudio2010.camera.qrcode.zxing.ZXingView;
import com.snailstudio2010.camera2.CameraView;
import com.snailstudio2010.camera2.Config;
import com.snailstudio2010.camera2.Properties;
import com.snailstudio2010.camera2.callback.CameraListener;
import com.snailstudio2010.camera2.callback.MenuInfo;
import com.snailstudio2010.camera2.callback.PictureListener;
import com.xuqiqiang.camera2.app.ui.CaptureAnimation;
import com.xuqiqiang.camera2.app.ui.CircleImageView;
import com.xuqiqiang.camera2.app.ui.CoverView;
import com.xuqiqiang.camera2.app.ui.GestureTouchListener;
import com.xuqiqiang.camera2.app.ui.IndicatorView;
import com.xuqiqiang.camera2.app.ui.ShutterButton;
import com.xuqiqiang.camera2.app.ui.filter.DialogFilter_1;
import com.xuqiqiang.camera2.app.ui.menu.CameraBaseMenu;
import com.xuqiqiang.camera2.app.ui.menu.CameraMenu;
import com.xuqiqiang.camera2.app.utils.Permission;
import com.xuqiqiang.camera2.app.utils.Utils;
import com.snailstudio2010.camera2.manager.CameraSettings;
import com.snailstudio2010.camera2.manager.CameraToolKit;
import com.snailstudio2010.camera2.module.CameraModule;
import com.snailstudio2010.camera2.module.PhotoModule;
import com.snailstudio2010.camera2.module.SingleCameraModule;
import com.snailstudio2010.camera2.module.VideoModule;
import com.snailstudio2010.camera2.ui.FocusView;
import com.snailstudio2010.camera2.utils.CameraUtil;
import com.snailstudio2010.camera2.utils.JobExecutor;
import com.snailstudio2010.camera2.utils.MediaFunc;

import org.wysaid.filter.AllFilters;
import org.wysaid.filter.base.BaseFilter;
import org.wysaid.filter.origin.NoneFilter;
import org.wysaid.filter.origin.SimpleFilter;

import java.util.Locale;

public class CameraActivity extends BaseActivity implements CameraBaseMenu.OnMenuClickListener {

    private static final String TAG = Config.getTag(CameraActivity.class);
    private static String[] mModules = {"拍 照", "视 频"};
    private CameraView mCameraView;
    private CameraModule mCameraModule;
    private CameraMenu mCameraMenu;
    private ViewGroup mBottomContainer;
    private CameraToolKit mToolKit;
    private IndicatorView mIndicatorView;

    private CoverView mCoverView;
    private TextView mZoomView;
    private CircleImageView mThumbnail;
    private ShutterButton mShutter;
    private View mMaskShutter;
    private ImageButton mSetting;
    private DialogFilter_1 mDialogFilter;
    private SeekBar mSeekBarFilter;
    private float mCameraZoom;

    private BaseFilter mCurrentFilter;
    private float mIntensity = 1f;
    private Properties mProperties;
    private QRCodeDetector mQRCodeDetector;
    private CaptureAnimation mCaptureAnimation;

    private MenuInfo mMenuInfo = new MenuInfo() {
        @Override
        public String[] getCameraIdList() {
            return mCameraModule.getDeviceManager().getCameraIdList();
        }

        @Override
        public String getCurrentCameraId() {
            return mCameraModule.getSettings().getGlobalPref(CameraSettings.KEY_CAMERA_ID);
        }

        @Override
        public String getCurrentValue(String key) {
            return mCameraModule.getSettings().getGlobalPref(key);
        }
    };

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraView = findViewById(R.id.camera_view);
        mBottomContainer = findViewById(R.id.bottom_container);
        mIndicatorView = findViewById(R.id.indicator_view);
        mCoverView = findViewById(R.id.cover_view);
        mZoomView = findViewById(R.id.zoom_view);
        mSeekBarFilter = findViewById(R.id.seek_filter_intensity);
//        mCoverView.setVisibility(View.GONE);
        mCameraView.setCoverView(mCoverView);
        mCameraView.setFocusView(new FocusView(this));
        mCameraView.addListener(new CameraListener() {
            private Runnable runnableHide;

            @Override
            public void setUIClickable(boolean clickable) {
                mShutter.setClickable(clickable);
                mThumbnail.setClickable(clickable);
                mSetting.setClickable(clickable);
                if (mCameraMenu != null && mCameraMenu.getView() != null)
                    mCameraMenu.getView().setClickable(clickable);
                mIndicatorView.setClickable(clickable);
                mCameraView.setClickable(clickable);
            }

//            @Override
//            public void changeModule(int right) {
//                Log.d(TAG, "changeModule:" + right);
//                mIndicatorView.select(1 - right);
//            }

            @Override
            public void closeMenu() {
                if (mCameraMenu != null)
                    mCameraMenu.close();
            }

            @Override
            public boolean updateUiSize(int width, int height) {
                Point mDisplaySize = CameraUtil.getDisplaySize(CameraActivity.this);
                int mVirtualKeyHeight = CameraUtil.getVirtualKeyHeight(CameraActivity.this);
                int mTopBarHeight = getResources()
                        .getDimensionPixelSize(R.dimen.menu_item_height);

                int realHeight = mDisplaySize.y + mVirtualKeyHeight;
                int bottomHeight = CameraUtil.getBottomBarHeight(mDisplaySize.x);
                FrameLayout.LayoutParams previewParams = new FrameLayout.LayoutParams(width, height);
                FrameLayout.LayoutParams bottomBarParams =
                        (FrameLayout.LayoutParams) mBottomContainer.getLayoutParams();
                int topMargin = 0;
                boolean needTopMargin = (height + 2 * mTopBarHeight) < realHeight;
                boolean needAlignCenter = width == height;
                if (needAlignCenter) {
                    topMargin = (realHeight - mTopBarHeight - mVirtualKeyHeight - height) / 2;
                } else if (needTopMargin) {
                    topMargin = mTopBarHeight;
                }
                int reservedHeight = realHeight - topMargin - height;
                boolean needAdjustBottomBar = reservedHeight > bottomHeight;
                if (needAdjustBottomBar) {
                    bottomHeight = reservedHeight;
                }
                // preview
                previewParams.setMargins(0, topMargin, 0, 0);
                mCameraView.setLayoutParams(previewParams);
                mZoomView.setLayoutParams(previewParams);
                // bottom bar
                bottomBarParams.height = bottomHeight;
                mBottomContainer.setPadding(0, 0, 0, mVirtualKeyHeight);
                mBottomContainer.setLayoutParams(bottomBarParams);

                FrameLayout.LayoutParams seekBarParams =
                        (FrameLayout.LayoutParams) mSeekBarFilter.getLayoutParams();
                seekBarParams.setMargins(0, 0, 0, bottomHeight);
                mSeekBarFilter.setLayoutParams(seekBarParams);
                return true;
            }

            @Override
            public void onZoomChanged(float currentZoom, float maxZoom) {
                Log.d(TAG, "onZoomChanged:" + currentZoom + "," + maxZoom);
                if (mZoomView.getVisibility() != View.VISIBLE) {
                    mZoomView.setVisibility(View.VISIBLE);
                }
                mZoomView.setText(String.format(Locale.US, "x%.1f", currentZoom));
                if (runnableHide != null)
                    mZoomView.removeCallbacks(runnableHide);
                runnableHide = new Runnable() {
                    @Override
                    public void run() {
                        mZoomView.setVisibility(View.INVISIBLE);
                    }
                };
                mZoomView.postDelayed(runnableHide, 500);
            }
        });
        mCameraView.setOnTouchListener(new GestureTouchListener(this, new GestureTouchListener.GestureListener() {
            @Override
            public void onClick(float x, float y) {
                ((SingleCameraModule) mCameraModule).onTouchToFocus(x, y);
            }

            @Override
            public void onScale(float factor) {
                mCameraZoom += factor;
                if (mCameraZoom > 1f) mCameraZoom = 1f;
                if (mCameraZoom < 0) mCameraZoom = 0;
                ((SingleCameraModule) mCameraModule).setCameraZoom(mCameraZoom);
                mCameraMenu.setSeekBarValue(CameraSettings.KEY_CAMERA_ZOOM, mCameraZoom);
            }

            @Override
            public void onSwipeLeft() {
                mIndicatorView.select(1);
            }

            @Override
            public void onSwipeRight() {
                mIndicatorView.select(0);
            }

            @Override
            public void onSwipe(float percent) {
            }

            @Override
            public void onCancel() {
                mCoverView.hide();
            }
        }));

        mProperties = new Properties()
//                .useCameraV1(true);
                .useGPUImage(true)
                .setGPUImageAssetsDir(this, "filter");

        mQRCodeDetector = new QRCodeDetector(this)
                .isShowLocationPoint(true)
                .setZoomListener(new ZXingView.ZoomListener() {
                    @Override
                    public float getCameraZoom() {
                        return mCameraZoom;
                    }

                    @Override
                    public void setCameraZoom(float value) {
                        mCameraZoom = value;
                        ((SingleCameraModule) mCameraModule).setCameraZoom(mCameraZoom);
                        mCameraMenu.setSeekBarValue(CameraSettings.KEY_CAMERA_ZOOM, mCameraZoom);
                    }
                })
                .setQRCodeListener(new ZXingView.QRCodeListener() {
                    @Override
                    public void onScanQRCodeSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
//                            mSpotAble = false;
                            mQRCodeDetector.pauseSpot();
                            new AlertDialog.Builder(CameraActivity.this)
                                    .setTitle("二维码")
                                    .setMessage(result)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
//                                    mSpotAble = true;
                                    mQRCodeDetector.startSpot((SingleCameraModule) mCameraModule);
                                }
                            }).create().show();
                        }
                    }

                    @Override
                    public void onCameraAmbientBrightnessChanged(boolean isDark) {
                    }
                });

        mShutter = findViewById(R.id.btn_shutter);
        mMaskShutter = findViewById(R.id.mask_shutter);
        mShutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureListener listener = new PictureListener() {
                    @Override
                    public void onShutter() {
                        if (mCaptureAnimation == null) {
                            mCaptureAnimation = new CaptureAnimation(mMaskShutter);
                        }
                        mCaptureAnimation.start();
                    }

                    @Override
                    public void onComplete(Uri uri, String path, Bitmap thumbnail) {
                        if (mThumbnail != null && thumbnail != null) {
                            mThumbnail.setImageBitmap(thumbnail);
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(CameraActivity.this, msg, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onVideoStart() {
                        mShutter.setMode(ShutterButton.VIDEO_RECORDING_MODE);
                    }

                    @Override
                    public void onVideoStop() {
                        mShutter.setMode(ShutterButton.VIDEO_MODE);
                    }
                };
                if (mCameraModule instanceof PhotoModule) {
                    ((PhotoModule) mCameraModule).takePicture(listener);
                } else if (mCameraModule instanceof VideoModule) {
                    ((VideoModule) mCameraModule).handleVideoRecording(listener);
                }
            }
        });

        mThumbnail = findViewById(R.id.thumbnail);
        mThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MediaFunc.goToGallery(CameraActivity.this);
                Utils.goToGallery(CameraActivity.this, MediaFunc.getCurrentUri());
            }
        });

        mSetting = findViewById(R.id.btn_setting);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CameraActivity.this, SettingsActivity.class));
            }
        });

        mToolKit = new CameraToolKit(this);

        for (String module : mModules) {
            mIndicatorView.addItem(module);
        }
        mIndicatorView.select(0);
        mIndicatorView.setIndicatorListener(new IndicatorView.IndicatorListener() {
            @Override
            public void onPositionChanged(int index) {
                Log.d(TAG, "onPositionChanged:" + index);
                if (index == 0) {
                    mCameraModule = new PhotoModule(mProperties);
                    mShutter.setMode(ShutterButton.PHOTO_MODE);
                } else if (index == 1) {
                    mCameraModule = new VideoModule(mProperties);
                    mShutter.setMode(ShutterButton.VIDEO_MODE);
                }
                mQRCodeDetector.startSpot((SingleCameraModule) mCameraModule);
                mCameraMenu.setSeekBarValue(CameraSettings.KEY_BRIGHTNESS, 0.5f);
                if (mCurrentFilter != null && !(mCurrentFilter instanceof NoneFilter))
                    ((SingleCameraModule) mCameraModule).setFilterConfig(mCurrentFilter.getConfig());
                mCameraView.setCameraModule(mCameraModule);
            }
        });

        mSeekBarFilter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mIntensity = progress / 100f;
//                mCurrentFilter.setIntensity(mIntensity);
//                Log.e(TAG, "onProgressChanged:" + mIntensity + ", " + mCurrentFilter.toString());
//                mCurrentFilter.applyFilterWithConfig(mGLSurfaceView);

                mIntensity = progress / 100f;
                mCurrentFilter.setIntensity(mIntensity);
                Log.e(TAG, "onProgressChanged:" + mIntensity + ", " + mCurrentFilter.toString());
//                mCurrentFilter.applyFilterWithConfig(mGLSurfaceView);
                ((SingleCameraModule) mCameraModule).setFilterConfig(mCurrentFilter.getConfig());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initMenu() {
        mCameraMenu = new CameraMenu(this, R.xml.menu_preference, mMenuInfo);
        mCameraMenu.setOnMenuClickListener(this);
        ViewGroup mMenuContainer = findViewById(R.id.menu_container);
        mMenuContainer.addView(mCameraMenu.getView());
    }

    private void updateThumbnail() {
        mToolKit.getExecutor().execute(new JobExecutor.Task<Void>() {
            @Override
            public Void run() {
                final Bitmap bitmap = MediaFunc.getThumb(CameraActivity.this);
                mToolKit.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap == null) {
                            mThumbnail.setClickable(false);
                            return;
                        }
                        mThumbnail.setImageBitmap(bitmap);
                        mThumbnail.setClickable(true);
                    }
                });
                return super.run();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Permission.checkPermission(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Permission.isPermissionGranted(this) && mCameraModule == null) {
            PreferenceManager.setDefaultValues(this, R.xml.camera_setting, false);
            mCameraModule = new PhotoModule(mProperties);
            mQRCodeDetector.startSpot((SingleCameraModule) mCameraModule);
//            ((SingleCameraModule)mCameraModule).setPreviewCallback(mQRCodeDetector);
//            ((SingleCameraModule)mCameraModule).setPreviewCallback(new SingleCameraModule.PreviewCallback() {
//
//                private long mLastPreviewFrameTime = 0;
//                protected ProcessDataTask mProcessDataTask;
//                boolean mSpotAble = true;
//                @Override
//                public void onPreviewFrame(byte[] data, Size size) {
//                    Log.d(TAG, "_test2_ onPreviewFrame");
//
//                    Log.d(TAG, "_test3_ 两次 onPreviewFrame 时间间隔：" + (System.currentTimeMillis() - mLastPreviewFrameTime));
//                    mLastPreviewFrameTime = System.currentTimeMillis();
//
////                    boolean mSpotAble = true;
//                    if (!mSpotAble || (mProcessDataTask != null && (mProcessDataTask.getStatus() == AsyncTask.Status.PENDING
//                            || mProcessDataTask.getStatus() == AsyncTask.Status.RUNNING))) {
//                        return;
//                    }
//                    Log.d(TAG, "_test3_ new ProcessDataTask");
//                    mProcessDataTask = new ProcessDataTask(((PhotoModule) mCameraModule).getUI().getRootView(), size,
//                            data, BGAQRCodeUtil.isPortrait(CameraActivity.this), new ZXingView.ZoomListener() {
//                        @Override
//                        public float getCameraZoom() {
//                            return mCameraZoom;
//                        }
//
//                        @Override
//                        public void setCameraZoom(float value) {
//                            mCameraZoom = value;
//                            ((SingleCameraModule) mCameraModule).setCameraZoom(mCameraZoom);
//                            mCameraMenu.setSeekBarValue(CameraSettings.KEY_CAMERA_ZOOM, mCameraZoom);
//                        }
//                    }, new ZXingView.QRCodeListener() {
//                        @Override
//                        public void onScanQRCodeSuccess(String result) {
//                            if(!TextUtils.isEmpty(result)) {
//                                mSpotAble = false;
//                                new AlertDialog.Builder(CameraActivity.this)
//                                .setTitle("二维码")
//                                .setMessage(result)
//                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                    }
//                                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                    @Override
//                                    public void onDismiss(DialogInterface dialog) {
//                                        mSpotAble = true;
//                                    }
//                                }).create().show();
//                            }
//                        }
//
//                        @Override
//                        public void onCameraAmbientBrightnessChanged(boolean isDark) {
//                            Log.d(TAG, "_test3_ onCameraAmbientBrightnessChanged:" + isDark);
//                        }
//                    }).perform();
//                }
//            });
            mCameraView.setCameraModule(mCameraModule);
            initMenu();
            updateThumbnail();
        }
        if (mCameraView != null)
            mCameraView.onResume();
        mCameraMenu.setSeekBarValue(CameraSettings.KEY_CAMERA_ZOOM, 0f);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100 && data != null) {
            mCurrentFilter = (BaseFilter) data.getSerializableExtra("filter");
            mCameraMenu.setSeekBarValue(CameraSettings.KEY_BRIGHTNESS, 0.5f);
            ((SingleCameraModule) mCameraModule).setFilterConfig(mCurrentFilter.getConfig());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraView != null)
            mCameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mToolKit.destroy();
    }

    @Override
    public void onMenuClick(String key, Object value) {
        switch (key) {
            case CameraSettings.KEY_SWITCH_CAMERA:
                ((SingleCameraModule) mCameraModule).switchCamera();
                break;
            case CameraSettings.KEY_FLASH_MODE:
                ((SingleCameraModule) mCameraModule).setFlashMode((String) value);
                break;
            case CameraSettings.KEY_CAMERA_ZOOM:
                Log.d(TAG, "KEY_CAMERA_ZOOM:" + value);
                mCameraZoom = (float) value;
                ((SingleCameraModule) mCameraModule).setCameraZoom(mCameraZoom);
                break;
            case CameraSettings.KEY_FOCUS_LENS:
                Log.d(TAG, "KEY_FOCUS_LENS:" + value);
                ((SingleCameraModule) mCameraModule).setFocusDistance((float) value);
                break;
            case CameraSettings.KEY_BRIGHTNESS:
                Log.d(TAG, "KEY_FOCUS_LENS:" + value);
//                ((SingleCameraModule) mCameraModule).setFocusDistance((float) value);
//                mCurrentFilter = ConstantFilters.FILTERS[mCurrentFilterId]
//                        + " @adjust brightness " + (progress - 50) / 50f;
//                mCameraView.setFilterWithConfig(mCurrentFilter);
                String config = mCurrentFilter == null ? "" : mCurrentFilter.getConfig();
                config += " @adjust brightness " + ((float) value - 0.5f) / 0.5f;
                ((SingleCameraModule) mCameraModule).setFilterConfig(config);
                break;
            case CameraSettings.KEY_FILTER:
                if (mDialogFilter == null) {
                    mDialogFilter = new DialogFilter_1(this);

                    /*滤镜对话框选择滤镜的监听*/
                    mDialogFilter.setOnFilterChangedListener(new DialogFilter_1.OnFilterChangedListener() {
                        @Override
                        public void onFilterChangedListener(final int position) {
                            mCameraMenu.setSeekBarValue(CameraSettings.KEY_BRIGHTNESS, 0.5f);
                            mCurrentFilter = AllFilters.FILTERS[position];
                            mCurrentFilter.setIntensity(mIntensity);
                            Log.e(TAG, "currentFilter:" + mCurrentFilter.toString());
//                            mCurrentFilter.applyFilterWithConfig(mGLSurfaceView);
                            ((SingleCameraModule) mCameraModule).setFilterConfig(mCurrentFilter.getConfig());

                            if (mCurrentFilter == null || mCurrentFilter instanceof NoneFilter
                                    || mCurrentFilter instanceof SimpleFilter) {
                                mSeekBarFilter.setVisibility(View.GONE);
                            } else {
                                mSeekBarFilter.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onMoreFilter() {
                            startActivityForResult(new Intent(CameraActivity.this,
                                    FiltersActivity.class), 100);
                        }
                    });

                    /*过滤对话框显示的监听*/
                    mDialogFilter.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            mBottomContainer.animate().alpha(0).setDuration(1000).start();
                        }
                    });
                    /*过滤对话框隐藏的监听*/
                    mDialogFilter.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mBottomContainer.animate().alpha(1).setDuration(1000).start();
                        }
                    });

                }
                mDialogFilter.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean fullScreen() {
        return true;
    }
}
