package com.cahemunoz.filters;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;

import jp.co.cyberagent.android.gpuimage.GPUImageView;



public class FiltersActivity extends AppCompatActivity implements FiltersFragment.Callback{
    public static final int REQUEST_FILTER = 3751;
    public static final String INTENT_INPUT_FILE_URI = "inputFile";
    public static final String INTENT_OUTPUT_FILE = "outputFile";
    public static final String INTENT_INPUT_IMAGE_WIDTH = "imageWidth";
    public static final String INTENT_INPUT_IMAGE_HEIGHT = "imageWidth";
    private static final int DEFAULT_IMAGE_SIZE = 1500;

    private FiltersFragment filtersFragment;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        Intent intent = getIntent();
        Uri inputFile = intent.getParcelableExtra(INTENT_INPUT_FILE_URI);
        String output = intent.getStringExtra(INTENT_OUTPUT_FILE);

        if(inputFile == null || output == null) {
            finish();
            return;
        }
        filtersFragment = (FiltersFragment) getFragmentManager().findFragmentById(R.id.fragment);
        loadImage();
    }

    public void loadImage() {
        Uri inputFile = getIntent().getParcelableExtra(INTENT_INPUT_FILE_URI);
        String output = getIntent().getStringExtra(INTENT_OUTPUT_FILE);
        int maxImageWidth = getIntent().getIntExtra(INTENT_INPUT_IMAGE_WIDTH, DEFAULT_IMAGE_SIZE);
        int maxImageHeight = getIntent().getIntExtra(INTENT_INPUT_IMAGE_HEIGHT, DEFAULT_IMAGE_SIZE);
        GPUImageView mImageView = (GPUImageView) findViewById(R.id.surfaceView);
        mImageView.setImage(inputFile); // this loads image on the current thread, should be run in a thread

        filtersFragment.setMaxImageHeight(maxImageHeight);
        filtersFragment.setMaxImageWidth(maxImageWidth);
        filtersFragment.setmGPUImage(mImageView);
        filtersFragment.setmGPUImage(mImageView);
        filtersFragment.setInputPhoto(inputFile);
        filtersFragment.setOutputPhoto(new File(output));
    }


    @Override
    public void onSave() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaved() {
        progressBar.setVisibility(View.GONE);
    }
}
