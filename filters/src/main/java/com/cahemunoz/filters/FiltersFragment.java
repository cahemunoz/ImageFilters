package com.cahemunoz.filters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceFilter;

/**
 * Created by Carlos Herrera Muñoz on 05/09/16.
 */
public class FiltersFragment extends Fragment {
    private static final float BRIGHTNESS_DEFAULT = 0.0f;
    private static final float CONTRAST_DEFAULT = 1.0f;
    private static final float SATURATION_DEFAULT = 1.0f;
    private static final float TEMPERATURE_DEFAULT = 5000.0f;
    private static final int START_VALUE = -100;
    private static final String TAG = FiltersFragment.class.getSimpleName();
    private LinearLayout filtersFrame;
    private LinearLayout brilhoButton;
    private LinearLayout contrasteButton;
    private LinearLayout saturacaoButton;
    private LinearLayout temperaturaButton;
    private LinearLayout controlFrame;
    private SeekBar seekBar;
    private TextView cancelButton;
    private TextView concluirButton;
    private TextView filterValue;
    private ActionBar actionBar;
    private MenuItem saveMenuItem;
    private GPUImageView mGPUImage;
    private GPUImageBrightnessFilter brightnessFilter;
    private GPUImageContrastFilter contrastFilter;
    private GPUImageSaturationFilter saturationFilter;
    private GPUImageWhiteBalanceFilter temperatureFilter;
    private GPUImageFilterGroup groupFilter;
    private float[] currentLevels = {BRIGHTNESS_DEFAULT, CONTRAST_DEFAULT, SATURATION_DEFAULT, TEMPERATURE_DEFAULT};
    private int filterType;
    private File outputPhoto;
    private Uri inputPhoto;


    public interface Callback {
        void onSave();
        void onSaved();
    }


    private SeekBar.OnSeekBarChangeListener actionSeekChange = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int value = progress + START_VALUE;
            filterValue.setText(String.valueOf(value));
            float normalized = 0.0f;

            if(filterType == R.string.brilho_filter) {
                normalized = (1.0f * progress + START_VALUE) / 100.0f;
                brightnessFilter.setBrightness(normalized);
                currentLevels[0] = normalized;
                //mGPUImage.setFilter(brightnessFilter);
            } else if (filterType == R.string.contraste_filter) {
                normalized = (2.0f * progress) / 100.0f;
                contrastFilter.setContrast(normalized);
                currentLevels[1] = normalized;
                //mGPUImage.setFilter(contrastFilter);
            } else if (filterType == R.string.saturacao_filter) {
                normalized = progress / 100.0f;
                saturationFilter.setSaturation(normalized);
                currentLevels[2] = normalized;
                //mGPUImage.setFilter(saturationFilter);
            } else if (filterType == R.string.temperatura_filter) {
                normalized = 15.0f * progress + 4000.0f;
                temperatureFilter.setTemperature(normalized);
                currentLevels[3] = normalized;
                //mGPUImage.setFilter(temperatureFilter);
            }
            mGPUImage.setFilter(groupFilter);
        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
    private View.OnClickListener actionCancelarFiltro = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filtersFrame.setVisibility(View.VISIBLE);
            controlFrame.setVisibility(View.INVISIBLE);
            actionBar.setTitle("");
            saveMenuItem.setEnabled(true);

            int normalized = 0;
            if(filterType == R.string.brilho_filter) {
                normalized = new Double((100.f * BRIGHTNESS_DEFAULT) - START_VALUE).intValue();
                seekBar.setProgress(normalized);
            } else if (filterType == R.string.contraste_filter) {
                normalized = new Double((100.0f * CONTRAST_DEFAULT) / 2.0f).intValue();
                seekBar.setProgress(normalized);
            } else if (filterType == R.string.saturacao_filter) {
                normalized = new Double(100.0f * SATURATION_DEFAULT).intValue();
                seekBar.setProgress(normalized);
            } else if (filterType == R.string.temperatura_filter) {
                normalized = new Double((TEMPERATURE_DEFAULT - 4000.f) / 15.0f).intValue();
                seekBar.setProgress(normalized);
            }
        }
    };

    ;
    private View.OnClickListener actionConcluir = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            filtersFrame.setVisibility(View.VISIBLE);
            controlFrame.setVisibility(View.INVISIBLE);
            actionBar.setTitle("");
            saveMenuItem.setEnabled(true);
        }
    };
    private View.OnClickListener actionClickFilter = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int filter = (int) v.getTag();
            actionBar.setTitle(getString(filter));
            filtersFrame.setVisibility(View.GONE);
            controlFrame.setVisibility(View.VISIBLE);
            saveMenuItem.setEnabled(false);
            filterType = filter;

            int normalized = 0;
            if(filterType == R.string.brilho_filter) {
                normalized = new Double((100.f * currentLevels[0]) - START_VALUE).intValue();
                seekBar.setProgress(normalized);
            } else if(filterType == R.string.contraste_filter) {
                normalized = new Double((100.0f * currentLevels[1]) / 2.0f).intValue();
                seekBar.setProgress(normalized);
            } else if(filterType == R.string.saturacao_filter) {
                normalized = new Double(100.0f * currentLevels[2]).intValue();
                seekBar.setProgress(normalized);
            } else if(filterType == R.string.temperatura_filter) {
                normalized = new Double((currentLevels[3] - 4000.f) / 15.0f).intValue();
                seekBar.setProgress(normalized);
            }
        }
    };

    public void onCreate(Bundle instanceState) {
        super.onCreate(instanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_filters, viewGroup, false);
        filtersFrame = (LinearLayout) view.findViewById(R.id.filters_frame);
        brilhoButton = (LinearLayout) view.findViewById(R.id.brilho_button);
        brilhoButton.setTag(R.string.brilho_filter);
        brilhoButton.setOnClickListener(actionClickFilter);

        contrasteButton = (LinearLayout) view.findViewById(R.id.contraste_button);
        contrasteButton.setTag(R.string.contraste_filter);
        contrasteButton.setOnClickListener(actionClickFilter);

        saturacaoButton = (LinearLayout) view.findViewById(R.id.saturacao_button);
        saturacaoButton.setTag(R.string.saturacao_filter);
        saturacaoButton.setOnClickListener(actionClickFilter);


        temperaturaButton = (LinearLayout) view.findViewById(R.id.temperatura_button);
        temperaturaButton.setTag(R.string.temperatura_filter);
        temperaturaButton.setOnClickListener(actionClickFilter);

        controlFrame = (LinearLayout) view.findViewById(R.id.control_frame);
        cancelButton = (TextView) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(actionCancelarFiltro);
        concluirButton = (TextView) view.findViewById(R.id.concluir_button);
        concluirButton.setOnClickListener(actionConcluir);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setMax(200);
        seekBar.setProgress(100);


        seekBar.setOnSeekBarChangeListener(actionSeekChange);

        filterValue = (TextView) view.findViewById(R.id.filter_value);

        controlFrame.setVisibility(View.INVISIBLE);
        filtersFrame.setVisibility(View.VISIBLE);

        brightnessFilter = new GPUImageBrightnessFilter();
        contrastFilter = new GPUImageContrastFilter();
        saturationFilter = new GPUImageSaturationFilter();
        temperatureFilter = new GPUImageWhiteBalanceFilter();
        groupFilter = new GPUImageFilterGroup();

        groupFilter.addFilter(brightnessFilter);
        groupFilter.addFilter(contrastFilter);
        groupFilter.addFilter(saturationFilter);
        groupFilter.addFilter(temperatureFilter);


        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filters, menu);
        saveMenuItem = menu.findItem(R.id.save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.save) {
            savePhoto();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePhoto() {
        final Callback callback = (Callback) getActivity();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                callback.onSave();
            }
            @Override
            protected void onPostExecute(Void result) {
                callback.onSaved();
                Intent intent = new Intent();
                intent.setData(Uri.fromFile(outputPhoto));
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    File file = outputPhoto;
                    if(file.getParentFile().exists())
                        file.getParentFile().mkdirs();
                    GPUImage tempImage = new GPUImage(getActivity());
                    InputStream stream = getActivity().getContentResolver().openInputStream(inputPhoto);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    tempImage.setFilter(groupFilter);
                    FileOutputStream outStream = new FileOutputStream(file);
                    Bitmap bitmapApply = tempImage.getBitmapWithFilterApplied(bitmap);
                    bitmapApply.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                    bitmap.recycle();
                    bitmapApply.recycle();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return null;
            }
        }.execute();
    }

    public void setmGPUImage(GPUImageView mGPUImage) {
        this.mGPUImage = mGPUImage;
    }

    public void setOutputPhoto(File outputPhoto) {
        this.outputPhoto = outputPhoto;
    }

    public void setInputPhoto(Uri inputPhoto) {
        this.inputPhoto = inputPhoto;
    }
}
