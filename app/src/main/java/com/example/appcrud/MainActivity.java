package com.example.appcrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String idSeller; // Variable que contendrá el id de cada cliente
    String idSales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText email = findViewById(R.id.etemail);
        EditText name = findViewById(R.id.etname);
        EditText phone = findViewById(R.id.etphone);
        TextView comision = findViewById(R.id.tvcommision);
        Button btnsave = findViewById(R.id.btnsave);
        Button btnsearch = findViewById(R.id.btnsearch);
        Button btnedit = findViewById(R.id.btnedit);
        Button btndelete = findViewById(R.id.btndelete);
        Button btnventas = findViewById(R.id.btnventas);


        //este bton eta en la ventana de seller y lleva a la vista de sales
        btnventas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ventas.class));
            }
        });
        //BOTON GUARDAR


    btnsave.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        saveSeller(email.getText().toString(),name.getText().toString(),phone.getText().toString(),comision.getText().toString());
        }

        private void saveSeller(String semail, String sname, String sphone, String scomision) {

            // Buscar la id entificación del cliente nuevo

            db.collection("seller")
                    .whereEqualTo("email", semail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    // Si no encuentra el documento
                                    // Guardar los datos del cliente (seller)

                                    Map<String, Object> seller = new HashMap<>(); // Tabla cursor
                                    seller.put("name", sname);
                                    seller.put("email", semail);
                                    seller.put("phone", sphone);
                                    seller.put("totalcomision", scomision);

                                    db.collection("seller")
                                            .add(seller)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getApplicationContext(), "Cliente agregado con éxito...", Toast.LENGTH_SHORT).show();

                                                    //Limpiar las cajas de texto
                                                    email.setText("");
                                                    name.setText("");
                                                    phone.setText("");
                                                    comision.setText("");
                                                    email.requestFocus(); //Enviar el foco al email

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Error! el cliente no se agregó...", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"El cliente ya existe, inténtelo con otro",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });


        }

    });


            //BOTON BUSCAR

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSeller(email.getText().toString());
            }

            private void searchSeller(String semail) {
                db.collection("seller")
                        .whereEqualTo("email",  email.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) { // Si encontró el documento
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            idSeller = document.getId();
                                            name.setText(document.getString("name"));
                                            phone.setText(document.getString("phone"));
                                            idSales = document.getId();
                                            comision.setText(document.getString("comision"));

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



                //BOTON BORRAR
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirmación de borrado
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("¿ Está seguro de eliminar el cliente con Id: " + email.getText().toString() + " ?");
                alertDialogBuilder.setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // Se eliminará el cliente con el id respectivo
                                db.collection("seller").document(idSeller)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MainActivity.this,"Cliente borrado correctamente...",Toast.LENGTH_SHORT).show();

                                                //Limpiar las cajas de texto
                                                email.setText("");
                                                name.setText("");
                                                phone.setText("");
                                                email.requestFocus(); //Enviar el foco al ident
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


                //BOTON EDITAR
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSeller(email.getText().toString(), name.getText().toString(), phone.getText().toString());
            }

            private void editSeller(String semail, String sname, String sphone) {
                Map<String, Object> mseller = new HashMap<>();
                mseller.put("email", semail);
                mseller.put("name", sname);
                mseller.put("phone", sphone);
                db.collection("seller").document(idSeller)
                        .set(mseller)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Cliente actualizado correctmente...",Toast.LENGTH_SHORT).show();

                                // Vaciar las cajas de texto
                                email.setText("");
                                name.setText("");
                                phone.setText("");
                                email.requestFocus(); //Enviar el foco al email
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}