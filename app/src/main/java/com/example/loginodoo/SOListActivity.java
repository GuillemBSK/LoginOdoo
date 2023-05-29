package com.example.loginodoo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class SOListActivity extends AppCompatActivity {
    private OdooUtility odoo;
    private String uid;
    private String password;
    private String serverAddress;
    private String database;
    private long searchTaskId;
    ListView listViewSaleOrder;
    List arrayListSaleOrder;
    Button buttonScan;
    private IntentIntegrator qrScan;
    String textViewName = "S0001";
    String textViewAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_list);

        buttonScan = (Button) findViewById(R.id.scanButton);

        uid = SharedData.getKey(SOListActivity.this, "uid");
        password = SharedData.getKey(SOListActivity.this, "password");
        serverAddress = SharedData.getKey(SOListActivity.this,
                "serverAddress");
        database = SharedData.getKey(SOListActivity.this, "database");
        odoo = new OdooUtility(serverAddress, "object");
        arrayListSaleOrder = new ArrayList();
        listViewSaleOrder = (ListView)
                findViewById(R.id.listViewSO);
        qrScan = new IntentIntegrator(this);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    textViewName = (obj.getString("name"));

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setMessage("Name: "+textViewName+" Address: "+textViewAddress)
                            .setTitle("SALE");

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast

                    // Crear un objeto AlertDialog.Builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    // Configurar el mensaje y el título de la ventana emergente
                    builder.setMessage("Name: "+textViewName)
                            .setTitle("SALE ORDER: ");


                    // Configurar el botón "Aceptar"
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onClickSearchSO (View v){
        EditText editKeyword = (EditText) findViewById(R.id.editKeywordSO);
        String keyword = editKeyword.getText().toString();
        List conditions = Arrays.asList(
                Arrays.asList(
                        Arrays.asList("name", "ilike", keyword)));
        Map fields = new HashMap() {{
            put("fields", Arrays.asList(
                    "id",
                    "name"
            ));
        }};
        searchTaskId = odoo.search_read(listener, database, uid, password,
                "sale.order", conditions, fields);
    }

    XMLRPCCallback listener = new XMLRPCCallback() {
        public void onResponse(long id, Object result) {
            Looper.prepare();
            if (id==searchTaskId)
            {
                Object[] classObjs=(Object[])result;
                int length=classObjs.length;
                if(length>0){
                    arrayListSaleOrder.clear();
                    for (int i=0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> classObj =
                                (Map<String,Object>)classObjs[i];
                        arrayListSaleOrder.add(classObj.get("name"));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillListSaleOrder();
                        }
                    });
                }
                else
                {
                    odoo.MessageDialog(SOListActivity.this,
                            "Sale Order not found");
                }
            }
            Looper.loop();
        }
        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            Log.e("SEARCH", error.getMessage());
            odoo.MessageDialog(SOListActivity.this, error.getMessage());
            Looper.loop();
        }
        public void onServerError(long id, XMLRPCServerException error) {
            Looper.prepare();
            Log.e("SEARCH", error.getMessage());
            odoo.MessageDialog(SOListActivity.this, error.getMessage());
            Looper.loop();
        }
    };
    private void fillListSaleOrder(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, arrayListSaleOrder);
        listViewSaleOrder.setAdapter(adapter);
        listViewSaleOrder.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id)
                    {
                        // ListView Clicked item index
                        int itemPosition = position;
                        // ListView Clicked item value
                        String itemValue = (String)
                                listViewSaleOrder.getItemAtPosition(position);
                        Intent myIntent = new Intent(SOListActivity.this, SOFormActivity.class);
                        myIntent.putExtra("name", itemValue);
                        SOListActivity.this.startActivity(myIntent);
                    }
                });
    }
}