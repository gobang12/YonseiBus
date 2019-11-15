package org.techtown.yonseibus;

import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Fragment1 extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        TextView text30 = rootView.findViewById(R.id.firstTime);
        TextView text34 = rootView.findViewById(R.id.firstTime2);
        TextView remainTime30 = rootView.findViewById(R.id.remainTime);
        TextView remainTime34 = rootView.findViewById(R.id.remainTime2);


        int nowTime = ((MainActivity) getActivity()).nowTime;

        int bushour = ((MainActivity) getActivity()).fastest30/100;
        int busmin = ((MainActivity) getActivity()).fastest30%100;
        String bushourString = Integer.toString(bushour);
        String busminString = Integer.toString(busmin);
        if(bushour < 10){
            bushourString = "0" + bushourString;
        }
        if(busmin < 10){
            busminString = "0" + busminString;
        }
        String buf = bushourString + ":" + busminString;

        if(bushour == 0 && busmin == 0){
            text30.setText("버스 없음");
            remainTime30.setText("") ;
            remainTime30.setVisibility(View.GONE);
        }
        else {
            text30.setText(buf);
            remainTime30.setText(Integer.toString((bushour - nowTime / 100) * 60 + busmin - nowTime % 100) + "분전");
        }

        bushour = ((MainActivity) getActivity()).fastest34/100;
        busmin = ((MainActivity) getActivity()).fastest34%100;
        bushourString = Integer.toString(bushour);
        busminString = Integer.toString(busmin);
        if(bushour < 10){
            bushourString = "0" + bushourString;
        }
        if(busmin < 10){
            busminString = "0" + busminString;
        }
        buf = bushourString + ":" + busminString;

        if(bushour == 0 && busmin == 0){
            text34.setText("버스 없음");
            remainTime34.setText("") ;
            remainTime34.setVisibility(View.GONE);
        }
        else {
            text34.setText(buf);
            remainTime34.setText(Integer.toString((bushour - nowTime / 100) * 60 + busmin - nowTime % 100) + "분전");
        }


        return rootView;
    }

}
