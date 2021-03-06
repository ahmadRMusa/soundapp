package com.github.qjcg.soundapp;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.qjcg.soundapp.R;
import com.github.qjcg.soundapp.common.ARRecorder;
import com.github.qjcg.soundapp.common.Recorder;
import com.github.qjcg.soundapp.utilities.PhoneClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;

public class RecordSoundFragment extends Fragment {

   public final static String ARG_START_RECORDING = "START_RECORDING";

   String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.wav";
   Recorder recorder = new ARRecorder(mFileName);
   PhoneClient client;

   ObjectAnimator recordBtnAnimator;
   ImageButton recordBtn ;
   TextView textView;

   @Override
   public void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);

      //mFileName = getFilesDir().getAbsolutePath() + "/test.wav";
      GoogleApiClient apiClient = new GoogleApiClient.Builder(getActivity()).addApi(Wearable.API).build();
      apiClient.connect();
      client = new PhoneClient(apiClient);
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      View v = inflater.inflate(R.layout.record_sound, container, false);

      recordBtn = (ImageButton) v.findViewById(R.id.record);
      recordBtn.setOnClickListener(new View.OnClickListener(){

         @Override
         public void onClick(View v) {
            toggleRecording();
         }
      });

      textView = (TextView) v.findViewById(R.id.textView);

      Bundle args = getArguments();
      if(args != null){
         if(args.getBoolean(ARG_START_RECORDING, false)){
            toggleRecording();
         }
      }

      return v;
   }

   public void toggleRecording() {
      if (!recorder.isRecording()) {
         recorder.startRecording();
         recordBtn.setImageResource(R.drawable.ic_stop);

         recordBtnAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0.5f, 1f).setDuration(500);
         recordBtnAnimator.setRepeatCount(ObjectAnimator.INFINITE);
         recordBtnAnimator.setRepeatMode(ObjectAnimator.REVERSE);
         recordBtnAnimator.start();

      } else {
         recordBtn.setImageResource(R.drawable.ic_mic);

         recordBtnAnimator.cancel();
         ObjectAnimator.ofFloat(textView, "alpha", 0).setDuration(500).start();

         recorder.stopRecording();

         client.sendSound(mFileName);

         MediaPlayer player = new MediaPlayer();
         try {
            player.setDataSource(mFileName);
            player.start();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
