package com.example.homework7;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class VideoActivity extends AppCompatActivity {
    private Button buttonPlay;
    private Button buttonStop;
    private Button buttonResume;
    private Button buttonSwitch;
    private VideoView videoView;
    private SeekBar seekBar;
    private TextView textViewTime;
    private TextView textViewCurrentPosition;

    private Uri uri;
    private boolean screenState;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (videoView.isPlaying()) {
                int current = videoView.getCurrentPosition();
                seekBar.setProgress(current);
                textViewCurrentPosition.setText(time(current));
            }
            handler.postDelayed(runnable, 500);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video);

        videoView = findViewById(R.id.videoView);
        videoView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        videoView.setZOrderOnTop(true);
        uri = Uri.parse("https://kvideo01.youju.sohu.com/fcf49ce9-fc00-4d1b-9111-ad16ee4266b51_0_0.mp4");
        videoView.setVideoURI(uri);
//        videoView.setVideoPath(getVideoPath(R.raw.big_buck_bunny));
        videoView.requestFocus();

        textViewTime = findViewById(R.id.tv_time);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        textViewCurrentPosition = findViewById(R.id.tv_current);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                textViewTime.setText(time(videoView.getDuration()));
                buttonPlay.setEnabled(true);
            }
        });

        buttonPlay = findViewById(R.id.buttonPlay);
        buttonPlay.setEnabled(false);
        buttonStop = findViewById(R.id.buttonStop);
        buttonResume = findViewById(R.id.buttonResume);
        buttonSwitch = findViewById(R.id.buttonSwitch);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        buttonResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.resume();
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    buttonPlay.setText("Play");
                    videoView.stopPlayback();
                }
            }
        });
        buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToLandScreen();
            }
        });

    }


//    private String getVideoPath(int resId) {
//        return "android.resource://" + this.getPackageName() + "/" + resId;
//    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (videoView.isPlaying()) {
                videoView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
    };

    protected void play() {
        if (buttonPlay.getText().equals("Play")) {
            buttonPlay.setText("Pause");
            handler.postDelayed(runnable, 0);
            videoView.start();
            seekBar.setMax(videoView.getDuration());
        } else {
            buttonPlay.setText("Play");
            videoView.pause();
        }
    }

    protected String time(long millionSeconds) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void convertToPortScreen(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置videoView竖屏播放
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip2px(VideoActivity.this, 198f));
        params.setMargins(dip2px(VideoActivity.this,12),dip2px(VideoActivity.this,15),
                dip2px(VideoActivity.this,12),dip2px(VideoActivity.this,14));
        //params.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoView.setLayoutParams(params);
        screenState = false;
    }

    private void convertToLandScreen(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置videoView全屏播放
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置videoView横屏播放
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        Point point = new Point();
        int API_LEVEL = android.os.Build.VERSION.SDK_INT;
        if(API_LEVEL >= 17){
            display.getRealSize(point);
        }else{
            display.getSize(point);
        }
        int height = point.y;
        int width = point.x;
//        Log.i(TAG,"screenHeight = "+height+" ; screenWidth = "+width);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoView
                .getLayoutParams(); // 取控当前的布局参数
        layoutParams.height = height;
        layoutParams.width = width;
        layoutParams.setMargins(0,0,0,0);
        videoView.setLayoutParams(layoutParams);

        screenState = true;
    }

    private static int dip2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp*scale+0.5f);
    }

}