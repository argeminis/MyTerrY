package com.urribarri.argenis.spotme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityMapsSave extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_save);

        final EditText et_info = (EditText) findViewById(R.id.save_info);
        Button btn_ok = (Button) findViewById(R.id.save_ok);
        Button btn_cancel = (Button) findViewById(R.id.save_cancel);

        /**OK click */
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el EditText no está vacío recogemos el resultado.
                if(et_info.getText().length()!=0) {
                    // Guardamos el texto del EditText en un String.
                    String resultado = et_info.getText().toString();
                    // Recogemos el intent que ha llamado a esta actividad.
                    Intent i = getIntent();
                    // Le metemos el resultado que queremos mandar a la
                    // actividad principal.
                    i.putExtra("RESULTADO", resultado);
                    // Establecemos el resultado, y volvemos a la actividad
                    // principal. La variable que introducimos en primer lugar
                    // "RESULT_OK" es de la propia actividad, no tenemos que
                    // declararla nosotros.
                    setResult(RESULT_OK, i);

                    // Finalizamos la Activity para volver a la anterior
                    finish();
                } else {
                    // Si no tenía nada escrito el EditText lo avisamos.
                    Toast.makeText(ActivityMapsSave.this, "No se ha introducido nada en el campo de texto",
                    Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**Cancel click */
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si se pulsa el botón, establecemos el resultado como cancelado.
                // Al igual que con "RESULT_OK", esta variable es de la activity.
                setResult(RESULT_CANCELED);

                // Finalizamos la Activity para volver a la anterior
                finish();

            }
        });
    }
}
