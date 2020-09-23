package com.xuqiqiang.camera2.app.ui;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.snailstudio2010.camera2.utils.CameraUtil;

/**
 * Created by xuqiqiang on 9/13/17.
 */
public class GestureTouchListener implements View.OnTouchListener {
    private static final String TAG = "GestureTouchListener";
    private static final long DELAY_TIME = 200;
    //    public float finger_spacing = 0;
//    public int zoom_level = 1;
    private float mClickDistance;
    private float mFlingDistance;
    private float mZoomDistance;
    private float mMaxDistance;
    private GestureListener mListener;
    private float mDownX;
    private float mDownY;
    private long mTouchTime;
    private long mDownTime;

    private boolean isMultiPointer;
    private float mFingerSpacing = -1;

    public GestureTouchListener(Context context, GestureListener listener) {
        mListener = listener;
        Point point = CameraUtil.getDisplaySize(context);
        mClickDistance = point.x / 20;
        mFlingDistance = point.x / 10;
        mZoomDistance = point.x;
        mMaxDistance = point.x / 5;

//        mScale = new ScaleGestureDetector(context, mScaleGestureListener);
    }

//    public void setGestureListener(GestureListener listener) {
//        mListener = listener;
//    }

    //    //确定前两个手指之间的空间
////    @SuppressWarnings("deprecation")
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void detectGesture(float downX, float upX, float downY, float upY) {
        float distanceX = upX - downX;
        float distanceY = upY - downY;
        if (Math.abs(distanceX) < mClickDistance
                && Math.abs(distanceY) < mClickDistance
                && mTouchTime < DELAY_TIME) {
            mListener.onClick(upX, upY);
        }
        if (Math.abs(distanceX) > mMaxDistance) {
            if (distanceX > 0) {
                mListener.onSwipeRight();
            } else {
                mListener.onSwipeLeft();
            }
        } else if (Math.abs(distanceX) > mClickDistance && mTouchTime < DELAY_TIME) {
            if (distanceX > 0) {
                mListener.onSwipeRight();
            } else {
                mListener.onSwipeLeft();
            }
        }
        if (Math.abs(distanceX) < mMaxDistance && mTouchTime > DELAY_TIME) {
            mListener.onCancel();
        }
    }

    private void detectSwipe(float downX, float moveX) {
        float alpha;
        if (Math.abs(moveX - downX) > mClickDistance) {
            alpha = ((int) (moveX - downX)) / mMaxDistance;
            if (alpha > 1f) {
                alpha = 1f;
            }
            if (alpha < -1f) {
                alpha = -1f;
            }
            mListener.onSwipe(alpha);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
////                if (true) {
//////            try {
////                int action = event.getAction();
////                float current_finger_spacing;
////
////                if (event.getPointerCount() > 1) {
////                    //多点触控逻辑
////                    current_finger_spacing = getFingerSpacing(event);
////                    if (finger_spacing != 0) {
////                        if (current_finger_spacing > finger_spacing && maxzoom > zoom_level) {
////                            zoom_level++;
////                        } else if (current_finger_spacing < finger_spacing && zoom_level > 1) {
////                            zoom_level--;
////                        }
//////                        int minW = (int) (m.width() / maxzoom);
//////                        int minH = (int) (m.height() / maxzoom);
//////                        int difW = m.width() - minW;
//////                        int difH = m.height() - minH;
//////                        int cropW = difW / 100 * (int) zoom_level;
//////                        int cropH = difH / 100 * (int) zoom_level;
//////                        cropW -= cropW & 3;
//////                        cropH -= cropH & 3;
//////                        Rect zoom = new Rect(cropW, cropH, m.width() - cropW, m.height() - cropH);
//////                        mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
////                    }
////                    finger_spacing = current_finger_spacing;
////                } else {
////                    if (action == MotionEvent.ACTION_UP) {
////                        //单触逻辑
////                    }
////                }
//
////                try {
////                    mCaptureSession
////                            .setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback, null);
////                } catch (CameraAccessException e) {
////                    e.printStackTrace();
////                } catch (NullPointerException ex) {
////                    ex.printStackTrace();
////                }
////            } catch (CameraAccessException e) {
////                ///抛出新的RuntimeException(无法访问摄像头。,e);
////
////                return true;
////            }
//        }
//        if(true) {
//
//            if (event.getPointerCount() > 1) {
//                float currentFingerSpacing = getFingerSpacing(event);
//                if(!isMultiPointer) {
//
//                }
//            }
////            mScale.onTouchEvent(event);
////            return true;
//        }
        Log.d(TAG, "test onTouch:" + event.getAction() + "," + event.getPointerCount());
        if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_DOWN) {
            mFingerSpacing = -1;
            isMultiPointer = false;
        }
//        if(event.getPointerCount() == 1 && (event.getAction() == MotionEvent.ACTION_DOWN
//                || event.getAction() == MotionEvent.ACTION_UP))
//            isMultiPointer = false;
        if (event.getPointerCount() > 1) {

            float currentFingerSpacing = getFingerSpacing(event);
            if (isMultiPointer) {
                mListener.onScale((currentFingerSpacing - mFingerSpacing) / mZoomDistance);
            }
            mFingerSpacing = currentFingerSpacing;
//            if(!isMultiPointer) {
//                mFingerSpacing = currentFingerSpacing;
//            } else {
//
//            }

            isMultiPointer = true;
            return true;
        }
        if (isMultiPointer) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                detectSwipe(mDownX, event.getX());
                break;
            case MotionEvent.ACTION_UP:
                mTouchTime = System.currentTimeMillis() - mDownTime;
                detectGesture(mDownX, event.getX(), mDownY, event.getY());
                break;
        }
        return true;
    }

//    /**
//     * 向外放缩标志
//     */
//    private static final int ZOOM_OUT = 0;
//    /**
//     * 向内放缩标志
//     */
//    private static final int ZOOM_IN = 1;

//    private ScaleGestureListener mScaleGestureListener = new ScaleGestureListener();
//
//    private ScaleGestureDetector mScale;

//    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        int mScaleFactor;
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
////            mScaleFactor = (int) detector.getScaleFactor();
//            Log.d(TAG, "onScale:" + detector.getScaleFactor());
////            Camera.Parameters params = camera.getParameters();
////            int zoom = params.getZoom();
////            if (mScaleFactor == ZOOM_IN) {
////                if (zoom < params.getMaxZoom())
////                    zoom += 1;
////            } else if (mScaleFactor == ZOOM_OUT) {
////                if (zoom > 0)
////                    zoom -= 1;
////            }
////            params.setZoom(zoom);
////            camera.setParameters(params);
//            mListener.onScale(detector.getScaleFactor());
//            return false;
//        }
//
//    }

    public interface GestureListener {
        void onClick(float x, float y);

        void onScale(float factor);

        void onSwipeLeft();

        void onSwipeRight();

        void onSwipe(float percent);

        void onCancel();
    }
}
