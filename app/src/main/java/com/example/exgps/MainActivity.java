package com.example.exgps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn01 = (Button)findViewById(R.id.btn01) ;
        btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationSVC() ;
            }
        });
        checkDangerousPermissions();
    }

    // 위치 정보를 확인하기 위한 메소드
    private void startLocationSVC(){
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE) ;

        // 리스너 인스턴스 생성
        GPSListener gpsListener = new GPSListener() ;
        long minTime = 10000 ;
        float minDistance = 0 ;

        try {
            // GPS Provider를 이용한 위치 요청
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDistance,gpsListener);
            //Network Provider를 이용한 위치 요청
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,minTime,minDistance,gpsListener);

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ;
            if(lastLocation != null){
                Double latitude = lastLocation.getLatitude() ;
                Double longitude = lastLocation.getLongitude() ;
                Toast.makeText(getApplicationContext(),
                        "Last Known Location -> Latitude : "+ latitude + "\nLongtitude : " + longitude,
                         Toast.LENGTH_LONG ).show() ;
            }

        }catch(SecurityException ex){
            ex.printStackTrace();
        }
        Toast.makeText(getApplicationContext(),
                        "위치 확인이 시작되었습니다. 로그 확인",Toast.LENGTH_SHORT).show();
    }

    private void checkDangerousPermissions(){
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED ;
        for(int i = 0 ; i < permissions.length; i++){
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]) ;
            if(permissionCheck == PackageManager.PERMISSION_DENIED) {
                break ;
            }
        }

        if( permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"권한있음",Toast.LENGTH_SHORT).show() ;
        }
        else {
            Toast.makeText(this,"권한없음",Toast.LENGTH_SHORT).show() ;

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함", Toast.LENGTH_SHORT).show();
            } else{
                ActivityCompat.requestPermissions(this,permissions,1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == 1){
            for(int i = 0 ; i < permissions.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,permissions[i] + "권한이 승인됨",Toast.LENGTH_LONG).show() ;
                } else {
                    Toast.makeText(this, permissions[i] + "권한이 승인되지 않음",Toast.LENGTH_SHORT).show() ;
                }
            }
        }
    }

    private class GPSListener implements LocationListener {
        @Override
        // LocationManager가 자동으로 호출하는 메소드 (위치 확인이 되었을 경우)
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude() ;
            Double longitude = location.getLongitude() ;

            String msg = "Latitude : "+ latitude + "\nLongtitude : " + longitude ;
            Log.i("GPSListener",msg) ;
            //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show() ;

            TextView viewGPS = (TextView) findViewById(R.id.txtGPS) ;
            viewGPS.setText(msg);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}
