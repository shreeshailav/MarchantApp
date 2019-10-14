/*
 * Copyright (C) 2008 ZXing authors
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

package com.journeyapps.barcodescanner;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class ViewfinderViewold extends View implements IViewFinder{


    private Rect mFramingRect;

    private static final float PORTRAIT_WIDTH_RATIO = 6f/8;
    private static final float PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f;

    private static final float LANDSCAPE_HEIGHT_RATIO = 5f/8;
    private static final float LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f;
    private static final int MIN_DIMENSION_DIFF = 50;

    private static final float DEFAULT_SQUARE_DIMENSION_RATIO = 5f / 8;

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private int scannerAlpha;
    private static final int POINT_SIZE = 10;
    private static final long ANIMATION_DELAY = 80l;

    private final int mDefaultLaserColor = getResources().getColor(R.color.zxing_custom_viewfinder_laser);
    private final int mDefaultMaskColor = getResources().getColor(R.color.zxing_custom_viewfinder_mask);
    private final int mDefaultBorderColor = getResources().getColor(R.color.zxing_custom_viewfinder_border);
    private final int mDefaultBorderStrokeWidth = getResources().getInteger(R.integer.zxing_custom_viewfinder_border_width);
    private final int mDefaultBorderLineLength = getResources().getInteger(R.integer.zxing_custom_viewfinder_border_length);

    protected Paint mLaserPaint;
    protected Paint mFinderMaskPaint;
    protected Paint mBorderPaint;
    protected int mBorderLineLength;
    protected boolean mSquareViewFinder;
    private boolean mIsLaserEnabled;
    private float mBordersAlpha;
    private int mViewFinderOffset = 0;



    protected static final String TAG = ViewfinderViewold.class.getSimpleName();

     protected static final int CURRENT_POINT_OPACITY = 0xA0;
    protected static final int MAX_RESULT_POINTS = 20;

    protected final Paint paint;
    protected Bitmap resultBitmap;
    protected int maskColor;
    protected final int resultColor;
    protected final int laserColor;
    protected final int resultPointColor;
     protected List<ResultPoint> possibleResultPoints;
    protected List<ResultPoint> lastPossibleResultPoints;
    protected CameraPreview cameraPreview;

    // Cache the framingRect and previewSize, so that we can still draw it after the preview
    // stopped.
    protected Rect framingRect;
    protected Size previewSize;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderViewold(Context context, AttributeSet attrs) {
        super(context, attrs);

       //  Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Resources resources = getResources();

        // Get setted attributes on view
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.zxing_finder);

        this.maskColor = attributes.getColor(R.styleable.zxing_finder_zxing_viewfinder_mask,
                resources.getColor(R.color.zxing_viewfinder_mask));
        this.resultColor = attributes.getColor(R.styleable.zxing_finder_zxing_result_view,
                resources.getColor(R.color.zxing_result_view));
        this.laserColor = attributes.getColor(R.styleable.zxing_finder_zxing_viewfinder_laser,
                resources.getColor(R.color.zxing_viewfinder_laser));
        this.resultPointColor = attributes.getColor(R.styleable.zxing_finder_zxing_possible_result_points,
                resources.getColor(R.color.zxing_possible_result_points));



         attributes.recycle();

        //set up laser paint
        mLaserPaint = new Paint();
        mLaserPaint.setColor(mDefaultLaserColor);
        mLaserPaint.setStyle(Paint.Style.FILL);

        //finder mask paint
        mFinderMaskPaint = new Paint();
        mFinderMaskPaint.setColor(mDefaultMaskColor);


        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(MAX_RESULT_POINTS);
        lastPossibleResultPoints = new ArrayList<>(MAX_RESULT_POINTS);

        //border paint
        mBorderPaint = new Paint();
        mBorderPaint.setColor(mDefaultBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mDefaultBorderStrokeWidth);
        mBorderPaint.setAntiAlias(true);
    }

    public void setCameraPreview(CameraPreview view) {
        this.cameraPreview = view;
        view.addStateListener(new CameraPreview.StateListener() {
            @Override
            public void previewSized() {
                refreshSizes();
                invalidate();
            }

            @Override
            public void previewStarted() {

            }

            @Override
            public void previewStopped() {

            }

            @Override
            public void cameraError(Exception error) {

            }

            @Override
            public void cameraClosed() {

            }
        });
    }

    protected void refreshSizes() {
        if(cameraPreview == null) {
            return;
        }
        Rect framingRect = cameraPreview.getFramingRect();
        Size previewSize = cameraPreview.getPreviewSize();
        if(framingRect != null && previewSize != null) {
            this.framingRect = framingRect;
            this.previewSize = previewSize;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewSize == null) {
            return;
        }

//        drawViewFinderMask(canvas);


        if (mIsLaserEnabled) {
            drawLaser(canvas);
        }


        final Rect frame = framingRect;
        final Size previewSize = this.previewSize;

        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        drawViewFinderBorder(canvas);


        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // Draw a red "laser scanner" line through the middle to show decoding is active
            paint.setColor(laserColor);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            final int middle = frame.height() / 2 + frame.top;
            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

            final float scaleX = this.getWidth() / (float) previewSize.width;
            final float scaleY = this.getHeight() / (float) previewSize.height;

            // draw the last possible result points
            if (!lastPossibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (final ResultPoint point : lastPossibleResultPoints) {
                    canvas.drawCircle(
                             (int) (point.getX() * scaleX),
                             (int) (point.getY() * scaleY),
                            radius, paint
                    );
                }
                lastPossibleResultPoints.clear();
            }

            // draw current possible result points
            if (!possibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (final ResultPoint point : possibleResultPoints) {
                    canvas.drawCircle(
                            (int) (point.getX() * scaleX),
                            (int) (point.getY() * scaleY),
                            POINT_SIZE, paint
                    );
                }

                // swap and clear buffers
                final List<ResultPoint> temp = possibleResultPoints;
                possibleResultPoints = lastPossibleResultPoints;
                lastPossibleResultPoints = temp;
                possibleResultPoints.clear();
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }


    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param result An image of the result.
     */
    public void drawResultBitmap(Bitmap result) {
        resultBitmap = result;
        invalidate();
    }

    /**
     * Only call from the UI thread.
     *
     * @param point a point to draw, relative to the preview frame
     */
    public void addPossibleResultPoint(ResultPoint point) {
        if (possibleResultPoints.size() < MAX_RESULT_POINTS)
            possibleResultPoints.add(point);
    }

    @Override
    public void setLaserColor(int laserColor) {
        mLaserPaint.setColor(laserColor);

    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
    }

    @Override
    public void setBorderColor(int borderColor) {
        mBorderPaint.setColor(borderColor);
    }

    @Override
    public void setBorderStrokeWidth(int borderStrokeWidth) {
        mBorderPaint.setStrokeWidth(borderStrokeWidth);

    }

    @Override
    public void setBorderLineLength(int borderLineLength) {
        mBorderLineLength = borderLineLength;

    }

    @Override
    public void setLaserEnabled(boolean isLaserEnabled) {
        mIsLaserEnabled = isLaserEnabled;
    }

    @Override
    public void setBorderCornerRounded(boolean isBorderCornersRounded) {
        if (isBorderCornersRounded) {
            mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        } else {
            mBorderPaint.setStrokeJoin(Paint.Join.BEVEL);
        }
    }

    public void drawViewFinderBorder(Canvas canvas) {
        Rect framingRect = getFramingRect();

        // Top-left corner
        Path path = new Path();
        path.moveTo(framingRect.left, framingRect.top + mBorderLineLength);
        path.lineTo(framingRect.left, framingRect.top);
        path.lineTo(framingRect.left + mBorderLineLength, framingRect.top);
        canvas.drawPath(path, mBorderPaint);

        // Top-right corner
        path.moveTo(framingRect.right, framingRect.top + mBorderLineLength);
        path.lineTo(framingRect.right, framingRect.top);
        path.lineTo(framingRect.right - mBorderLineLength, framingRect.top);
        canvas.drawPath(path, mBorderPaint);

        // Bottom-right corner
        path.moveTo(framingRect.right, framingRect.bottom - mBorderLineLength);
        path.lineTo(framingRect.right, framingRect.bottom);
        path.lineTo(framingRect.right - mBorderLineLength, framingRect.bottom);
        canvas.drawPath(path, mBorderPaint);

        // Bottom-left corner
        path.moveTo(framingRect.left, framingRect.bottom - mBorderLineLength);
        path.lineTo(framingRect.left, framingRect.bottom);
        path.lineTo(framingRect.left + mBorderLineLength, framingRect.bottom);
        canvas.drawPath(path, mBorderPaint);
    }
    public Rect getFramingRect() {
        return mFramingRect;
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }
    public synchronized void updateFramingRect() {
        Point viewResolution = new Point(getWidth(), getHeight());
        int width;
        int height;
        int orientation = DisplayUtils.getScreenOrientation(getContext());

        if(mSquareViewFinder) {
            if(orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (int) (getHeight() * DEFAULT_SQUARE_DIMENSION_RATIO);
                width = height;
            } else {
                width = (int) (getWidth() * DEFAULT_SQUARE_DIMENSION_RATIO);
                height = width;
            }
        } else {
            if(orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (int) (getHeight() * LANDSCAPE_HEIGHT_RATIO);
                width = (int) (LANDSCAPE_WIDTH_HEIGHT_RATIO * height);
            } else {
                width = (int) (getWidth() * PORTRAIT_WIDTH_RATIO);
                height = (int) (PORTRAIT_WIDTH_HEIGHT_RATIO * width);
            }
        }

        if(width > getWidth()) {
            width = getWidth() - MIN_DIMENSION_DIFF;
        }

        if(height > getHeight()) {
            height = getHeight() - MIN_DIMENSION_DIFF;
        }

        int leftOffset = (viewResolution.x - width) / 2;
        int topOffset = (viewResolution.y - height) / 2;
        mFramingRect = new Rect(leftOffset + mViewFinderOffset, topOffset + mViewFinderOffset, leftOffset + width - mViewFinderOffset, topOffset + height - mViewFinderOffset);
    }
    @Override
    public void setBorderAlpha(float alpha) {
        int colorAlpha = (int) (255 * alpha);
        mBordersAlpha = alpha;
        mBorderPaint.setAlpha(colorAlpha);
    }

    @Override
    public void setBorderCornerRadius(int borderCornersRadius) {
        mBorderPaint.setPathEffect(new CornerPathEffect(borderCornersRadius));
    }

    @Override
    public void setViewFinderOffset(int offset) {
        mViewFinderOffset = offset;

    }

    @Override
    public void setSquareViewFinder(boolean isSquareViewFinder) {
        mSquareViewFinder = isSquareViewFinder;

    }

    @Override
    public void setupViewFinder() {
        updateFramingRect();
        invalidate();
    }
    public void drawLaser(Canvas canvas) {
        Rect framingRect = getFramingRect();

        // Draw a red "laser scanner" line through the middle to show decoding is active
        mLaserPaint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = framingRect.height() / 2 + framingRect.top;
        canvas.drawRect(framingRect.left + 2, middle - 1, framingRect.right - 1, middle + 2, mLaserPaint);

        postInvalidateDelayed(ANIMATION_DELAY,
                framingRect.left - POINT_SIZE,
                framingRect.top - POINT_SIZE,
                framingRect.right + POINT_SIZE,
                framingRect.bottom + POINT_SIZE);
    }
    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Rect framingRect = getFramingRect();

        canvas.drawRect(0, 0, width, framingRect.top, mFinderMaskPaint);
        canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom + 1, mFinderMaskPaint);
        canvas.drawRect(framingRect.right + 1, framingRect.top, width, framingRect.bottom + 1, mFinderMaskPaint);
        canvas.drawRect(0, framingRect.bottom + 1, width, height, mFinderMaskPaint);
    }
}
