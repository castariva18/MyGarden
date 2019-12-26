package com.example.android.mygarden;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.mygarden.provider.PlantContract.PATH_PLANTS;
import static com.example.android.mygarden.provider.PlantContract.PlantEntry.COLUMN_CREATION_TIME;
import static com.example.android.mygarden.provider.PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME;
import static com.example.android.mygarden.provider.PlantContract.PlantEntry.COLUMN_PLANT_TYPE;
import static com.example.android.mygarden.utils.PlantUtils.MAX_AGE_WITHOUT_WATER;

public class PlantWateringService extends IntentService {

    public static final String ACTION_WATER_PLANTS = "com.example.android.mygarden.action.water_plant";
    public static final String ACTION_UPDATE_PLANTS = "com.example.android.mygarden.action.update_plant";

    public PlantWateringService() {
        super("PlantWteringService");
    }

    public static void startActionWaterPlants(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANTS);
        context.startService(intent);
    }
        public static void startActionUpdatePlants(Context context) {
            Intent intent = new Intent(context, PlantWateringService.class);
            intent.setAction(ACTION_UPDATE_PLANTS);
            context.startService(intent);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_WATER_PLANTS.equals(action)) {
                    handleActionWaterPlants();
                } else if (ACTION_UPDATE_PLANTS.equals(action)) {
                    handleActionUpdatePlants();
                }
            }
        }

        private void handleActionUpdatePlants() {
            Uri PLANTS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
            Cursor cursor = getContentResolver().query(PLANTS_URI, null, null, null, COLUMN_LAST_WATERED_TIME);

            int imgRes = R.drawable.grass;
            if (cursor != null && cursor.moveToNext()) {
                int createIndex = cursor.getColumnIndex(COLUMN_CREATION_TIME);
                int waterIndex = cursor.getColumnIndex(COLUMN_LAST_WATERED_TIME);
                int typeIndex = cursor.getColumnIndex(COLUMN_PLANT_TYPE);

                long now = System.currentTimeMillis();
                long wateredAt = cursor.getLong(waterIndex);
                long createdAt = cursor.getLong(createIndex);

                int plantType = cursor.getInt(typeIndex);

                imgRes = PlantUtils.getPlantImageRes(this, now - createdAt, now - wateredAt, plantType);

            }

            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            int[] ids = manager.getAppWidgetIds(new ComponentName(this, PlantWidgetProvider.class));
            PlantWidgetProvider.updateAppWidget(this, manager, imgRes,ids);
        }

        private void handleActionWaterPlants() {
            Uri PLANTS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
            ContentValues values = new ContentValues();
            long timeNow = System.currentTimeMillis();

            values.put(COLUMN_LAST_WATERED_TIME, timeNow);

            getContentResolver().update(
                    PLANTS_URI,
                    values,
                    COLUMN_LAST_WATERED_TIME + ">?",
                    new String[] {String.valueOf(timeNow - MAX_AGE_WITHOUT_WATER)}
            );
        }
    }
