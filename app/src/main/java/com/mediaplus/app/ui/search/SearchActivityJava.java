package com.mediaplus.app.ui.search;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.mediaplus.app.databinding.ActivitySearchBinding;

/**
 * Java wrapper for the SearchActivity to ensure backward compatibility.
 */
public class SearchActivityJava extends AppCompatActivity {
    
    private ActivitySearchBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Launch the Kotlin-based implementation
        startActivity(new android.content.Intent(this, SearchActivity.class));
        finish();
    }
}
