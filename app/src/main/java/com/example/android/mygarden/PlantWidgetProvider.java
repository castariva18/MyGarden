package com.example.android.mygarden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.mygarden.ui.MainActivity;

public class PlantWidgetProvider extends AppWidgetProvider {

    static void updateWidget(Context context,AppWidgetManager manager, int imageres,int id){
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,i,0);
        RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.plant_widget);
        view.setOnClickPendingIntent(R.id.plant_image,pi);

        Intent waterIntent = new Intent(context,PlantWateringService.class);
        waterIntent.setAction(PlantWateringService.ACTION_WATER_PLANTS);
        PendingIntent pIntent = PendingIntent.getService(context,0,waterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setImageViewResource(R.id.plant_image,imageres);
        view.setOnClickPendingIntent(R.id.water_image,pIntent);

        manager.updateAppWidget(id,view);
    }

    static void updateAppWidget (Context context, AppWidgetManager manager,int imageres, int[] ids) {

        for (int id : ids) {
            updateWidget(context,manager,imageres,id);
        }
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        PlantWateringService.startActionUpdatePlants(context);
        }
    }


