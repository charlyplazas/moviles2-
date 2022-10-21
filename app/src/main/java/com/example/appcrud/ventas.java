package com.example.appcrud;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class ventas extends AppCompatActivity {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    String idSeler;
    String selerEmail;
    String vname;
    String vphone;
    String vcomision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);
        TextView emailVentas = findViewById(R.id.etemailventas);
        TextView valorVentas = findViewById(R.id.etsetvalueventas);
        TextView dateVentas = findViewById(R.id.etdateventas);
        Button btnguardarventas = findViewById(R.id.btnguardarventas);
        Button btnatras = findViewById(R.id.btnatras);


        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),principal.class));
            }
        });

        btnguardarventas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSeller(emailVentas.getText().toString(),valorVentas.getText().toString(),dateVentas.getText().toString());
                db.collection("seller")
                        .whereEqualTo("email",  emailVentas.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) { // Si encontró el documento
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            idSeler = document.getId();
                                            vname= document.getString("name");
                                            vphone = document.getString("phone");
                                            selerEmail = document.getString("email");
                                            vcomision = document.getString("comision");

                                            Toast.makeText(getApplicationContext(),"Estoy buscando y guardando...",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"Este cliente no se encuentra en nuestra base de datos",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }



        });


    }
    private void saveSeller(String semailVentas, String svalorVentas, String sdateVentas) {

        // Buscar la id entificación del cliente nuevo

        db.collection("sales")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (parseInt(sdateVentas) >= 10000000) {
                            if (task.isSuccessful()) {
                                Map<String, Object> sales = new HashMap<>(); // Tabla cursor
                                sales.put("emailVentas", semailVentas);
                                sales.put("valorVentas", svalorVentas);
                                sales.put("dateVentas", sdateVentas);


                                db.collection("sales")
                                        .add(sales)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getApplicationContext(), "Cliente agregado con éxito...", Toast.LENGTH_SHORT).show();
//
//                                                //Limpiar las cajas de texto
//                                                emailVentas.setText("");
//                                                valorVentas.setText("");
//                                                dateVentas.setText("");
//                                                emailVentas.requestFocus(); //Enviar el foco al email

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error! el cliente no se agregó...", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                String comision=String.valueOf(parseInt(sdateVentas)*2/100);

                                Map<String, Object> mseller = new HashMap<>();
                                mseller.put("email", selerEmail);
                                mseller.put("name", vname);
                                mseller.put("phone", vphone);
                                if(task.isSuccessful()){
                                    if(vcomision == null){

                                        mseller.put("comision",comision);
                                        db.collection("seller").document(idSeler)
                                                .set(mseller)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ventas.this,"Comision agregada",Toast.LENGTH_SHORT).show();

//                                                        // Vaciar las cajas de texto
//                                                        email.setText("");
//                                                        name.setText("");
//                                                        phone.setText("");
//                                                        email.requestFocus(); //Enviar el foco al email
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ventas.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }else{
                                        comision=String.valueOf(parseInt(vcomision) + parseInt(comision));
                                        mseller.put("comision",comision);
                                        db.collection("seller").document(idSeler)
                                                .set(mseller)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ventas.this,"Comision sumada con exitó",Toast.LENGTH_SHORT).show();

//                                                        // Vaciar las cajas de texto
//                                                        email.setText("");
//                                                        name.setText("");
//                                                        phone.setText("");
//                                                        email.requestFocus(); //Enviar el foco al email
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ventas.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                            }

                        } else
                        {
                            Toast.makeText(getApplicationContext(),"El valor de la venta debe de ser mayor o igual a 10 Millones",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}