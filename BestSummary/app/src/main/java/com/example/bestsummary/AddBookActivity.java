package com.example.bestsummary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.CDATASection;

import java.util.HashMap;

public class AddBookActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE = 101 ;
    private ImageView imageAdd;
    private TextView imgAddText;
    private TextView backText;
    private EditText editAddBook, editAuthor;
    private Button addButton;
    private ProgressBar addProgress;

    long maxid=0;

    Uri imageUri;
    boolean isImageAdd = false;

    DatabaseReference db;
    StorageReference sReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        imageAdd = findViewById(R.id.imageAdd);
        imgAddText = findViewById(R.id.imgAddText);
        backText = findViewById(R.id.backText);
        editAddBook = findViewById(R.id.editBookName);
        editAuthor = findViewById(R.id.editAuthor);
        addButton = findViewById(R.id.addButton);
        addProgress = findViewById(R.id.addProgress);

        db = FirebaseDatabase.getInstance().getReference().child("Books");
        sReff = FirebaseStorage.getInstance().getReference().child("bookImages");

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddBookActivity.this, HomeActivity.class));
            }
        });



        imgAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bookName = editAddBook.getText().toString();
                final String author = editAuthor.getText().toString();


                Query bookQuery = FirebaseDatabase.getInstance().getReference().child("Books").orderByChild("BookName").equalTo(bookName);
                Query authorQuery = FirebaseDatabase.getInstance().getReference().child("Books").orderByChild("Author").equalTo(author);
                bookQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(bookName.isEmpty()){
                            editAddBook.setError("Please enter book name!");
                            editAddBook.requestFocus();
                            return;
                        }

                        if(author.isEmpty()){
                            editAuthor.setError("Please enter author name!");
                            editAuthor.requestFocus();
                            return;
                        }

                        if(isImageAdd == false) {
                            Toast.makeText(AddBookActivity.this,
                                    "Please upload an image!", Toast.LENGTH_SHORT).show();
                        }

                        if(isImageAdd != false && editAddBook.length() > 0 && editAuthor.length() > 0) {
                            if(snapshot.getChildrenCount() > 0) {
                                authorQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                        if(snapshot2.getChildrenCount() > 0){
                                            Toast.makeText(AddBookActivity.this,
                                                    "This book already in list. Please use search.", Toast.LENGTH_SHORT).show();
                                        }
                                        else    {
                                            uploadBook(bookName, author);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            else    {
                                uploadBook(bookName, author);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    maxid = (snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void uploadBook(String bookName, String author)   {
            addProgress.setVisibility(View.VISIBLE);

            final String key = db.push().getKey();
            sReff.child(key+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sReff.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("BookName", bookName);
                            hashMap.put("Author", author);
                            hashMap.put("ImageUrl", uri.toString());

                            db.child(String.valueOf(maxid+1)).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    addProgress.setVisibility(View.GONE);
                                    Toast.makeText(AddBookActivity.this, "Succesfully Uploaded!",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddBookActivity.this, HomeActivity.class));
                                }
                            });
                        }
                    });
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_IMAGE && data != null) {
            imageUri = data.getData();
            isImageAdd = true;
            imgAddText.setVisibility(View.GONE);
            imageAdd.setImageURI(imageUri);
        }
    }
}