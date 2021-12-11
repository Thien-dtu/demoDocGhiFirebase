package com.example.danhba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.danhba.adapte.danhbaAdapter;
import com.example.danhba.model.danhba;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ListView lvContact;
    danhbaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        getContactsFromFirebase();
        addEvents();
    }

    private void addEvents() {

        lvContact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final danhba contact=adapter.getItem(position);
                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                DatabaseReference myRef= firebaseDatabase.getReference("contacts");
                myRef.child(contact.getContactId()).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Thành công",Toast.LENGTH_LONG).show();
                                adapter.remove(contact);
                            }})
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Lỗi rồi :"+ e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                return false;
            }
        });
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                danhba data=adapter.getItem(position);
                Intent intent=new Intent(MainActivity.this,CapNhatContactActivity.class);
                intent.putExtra("KEY",data.getContactId());
                startActivity(intent);
            }
        });
    }

    private void getContactsFromFirebase() {
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myRef= firebaseDatabase.getReference("contacts");
        adapter.clear();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dss : dataSnapshot.getChildren())
                {
                    //convert ra đối tượng Contact:
                    danhba contact=dss.getValue(danhba.class);
                    String key=dss.getKey();
                    contact.setContactId(key);
                    adapter.add(contact);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addControls() {
        lvContact=findViewById(R.id.lvContact);
        adapter=new danhbaAdapter(this,R.layout.item);
        lvContact.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.mnuAdd)
        {
            //mở màn hình thêm ở đây
            Intent intent=new Intent(MainActivity.this,ThemContactActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
