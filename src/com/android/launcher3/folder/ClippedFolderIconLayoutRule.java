package com.android.launcher3.folder;

import com.android.launcher3.DeviceProfile;

public class ClippedFolderIconLayoutRule {

    public static final int MAX_NUM_ITEMS_IN_PREVIEW = DeviceProfile.FOLDER_PREVIEW_ROW_COLUMN * DeviceProfile.FOLDER_PREVIEW_ROW_COLUMN;
    private static final int MIN_NUM_ITEMS_IN_PREVIEW = 2;

    private static final float MIN_SCALE = 0.44f;
    private static final float MAX_SCALE = 0.51f;
    private static final float MAX_RADIUS_DILATION = 0.25f;
    // The max amount of overlap the preview items can go outside of the background bounds.
    public static final float ICON_OVERLAP_FACTOR = 1 + (MAX_RADIUS_DILATION / 2f);
    private static final float ITEM_RADIUS_SCALE_FACTOR = 1.15f;

    public static final int EXIT_INDEX = -2;
    public static final int ENTER_INDEX = -3;

    private float[] mTmpPoint = new float[2];

    private float mAvailableSpace;
    private float mRadius;
    private float mIconSize;
    private boolean mIsRtl;
    private float mBaselineIconScale;

    public void init(int availableSpace, float intrinsicIconSize, boolean rtl) {
        mAvailableSpace = availableSpace;
        mRadius = ITEM_RADIUS_SCALE_FACTOR * availableSpace / 2f;
        mIconSize = intrinsicIconSize;
        mIsRtl = rtl;
        mBaselineIconScale = availableSpace / (intrinsicIconSize * 1f);
    }

    public PreviewItemDrawingParams computePreviewItemDrawingParams(int index, int curNumItems,
                                                                    PreviewItemDrawingParams params) {
        float totalScale = scaleForItem(curNumItems);
        float transX;
        float transY;

        if (index == EXIT_INDEX) {
            // 0 1 * <-- Exit position (row 0, col 2)
            // 2 3
            getGridPosition(0, 2, mTmpPoint);
        } else if (index == ENTER_INDEX) {
            // 0 1
            // 2 3 * <-- Enter position (row 1, col 2)
            getGridPosition(1, 2, mTmpPoint);
        } else if (index >= MAX_NUM_ITEMS_IN_PREVIEW) {
            // Items beyond those displayed in the preview are animated to the center
            mTmpPoint[0] = mTmpPoint[1] = mAvailableSpace / 2 - (mIconSize * totalScale) / 2;
        } else {
            getPosition(index, curNumItems, mTmpPoint);
        }

        transX = mTmpPoint[0];
        transY = mTmpPoint[1];

        if (params == null) {
            params = new PreviewItemDrawingParams(transX, transY, totalScale);
        } else {
            params.update(transX, transY, totalScale);
        }
        return params;
    }

    /**
     * Builds a grid based on the positioning of the items when there are
     * {@link #MAX_NUM_ITEMS_IN_PREVIEW} in the preview.
     * <p>
     * Positions in the grid: 0 1  // 0 is row 0, col 1
     * 2 3  // 3 is row 1, col 1
     */
    private void getGridPosition(int row, int col, float[] result) {
        // We use position 0 and 3 to calculate the x and y distances between items.
        getPosition(0, 4, result);
        float left = result[0];
        float top = result[1];

        getPosition(DeviceProfile.FOLDER_PREVIEW_ROW_COLUMN + 1, 4, result);
        float dx = result[0] - left;
        float dy = result[1] - top;

        result[0] = left + (col * dx);
        result[1] = top + (row * dy);
    }

    private void getPosition(int index, int curNumItems, float[] result) {
        int row = index / DeviceProfile.FOLDER_PREVIEW_ROW_COLUMN;
        int column = index % DeviceProfile.FOLDER_PREVIEW_ROW_COLUMN;
        float iconSize = mIconSize * scaleForItem(curNumItems);
        float padding = mAvailableSpace * 0.13f;
        float gap = (mAvailableSpace - padding * 2 - iconSize * DeviceProfile.FOLDER_PREVIEW_ROW_COLUMN) / (DeviceProfile.FOLDER_PREVIEW_ROW_COLUMN - 1);
        result[0] = padding + column * (gap + iconSize);
        result[1] = padding + row * (gap + iconSize);
    }

    public float scaleForItem(int numItems) {
        return 0.23f * mAvailableSpace / 240 * 3 / DeviceProfile.FOLDER_PREVIEW_ROW_COLUMN;
    }

    public float getIconSize() {
        return mIconSize;
    }
}
