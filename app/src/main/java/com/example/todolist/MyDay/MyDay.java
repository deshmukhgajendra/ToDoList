package com.example.todolist.MyDay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;

public class MyDay extends AppCompatActivity {

    FloatingActionButton fab;
    public String task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_myday);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fab=findViewById(R.id.fab);

//        LayoutInflater inflater=getLayoutInflater();
//        View view= inflater.inflate(R.layout.bottonsheetlayout,null,false);
//        RadioButton taskButton = view .findViewById(R.id.tasksButton);
//        Button addButton= view .findViewById(R.id.addButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View bottomSheetView = inflater.inflate(R.layout.bottonsheetlayout, null);

                EditText taskEditText=view.findViewById(R.id.taskEditText);
                MaterialRadioButton taskButton=view.findViewById(R.id.tasksButton);
                Button addButton=view.findViewById(R.id.addButton);

                // Create and show the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MyDay.this);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        task=taskEditText.getText().toString();
                    }
                });
            }
        });
    }
}