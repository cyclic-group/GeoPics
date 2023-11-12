package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.databinding.UploadPhotoFragmentBinding
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.model.GTPhoto
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class UploadPhotoFragment : Fragment() {

    companion object {
        fun newInstance() = UploadPhotoFragment()

    }

    private lateinit var viewModel: UploadPhotoViewModel
    private var _binding: UploadPhotoFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickLauncher: ActivityResultLauncher<Intent>
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[UploadPhotoViewModel::class.java]
        _binding = UploadPhotoFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.controller = this
        binding.viewModel = viewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawable = viewModel.getImageContent() ?: ActivityCompat.getDrawable(requireActivity(),R.drawable.ic_default)
        binding.imageviewPhoto.setImageDrawable(drawable)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK){
                val imageBitmap = it.data?.extras?.get("data") as Bitmap
                binding.imageviewPhoto.setImageBitmap(imageBitmap)
            } else {
                Toast.makeText(requireActivity(), getString(R.string.prompt_camera_result_not_ok), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        pickLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                binding.imageviewPhoto.setImageURI(it.data?.data)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.setImageContent(binding.imageviewPhoto.drawable)
    }

    fun onTakePhoto(view: View) {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            cameraLauncher.launch(takePhotoIntent)
        } catch (e: Exception) {
            Toast.makeText(requireActivity(),getString(R.string.prompt_launch_camera_fail),Toast.LENGTH_LONG)
                .show()
        }
    }

    fun onChoosePhoto(view: View) {
        if (ContextCompat.checkSelfPermission(requireActivity().applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            Toast.makeText(requireActivity(),getString(R.string.prompt_no_read_storage_permission),Toast.LENGTH_SHORT)
                .show()

        } else {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = "image/*"
            try {
                pickLauncher.launch(pickIntent)
            } catch (e: Exception) {
                Toast.makeText(requireActivity(),getString(R.string.prompt_fail_launch_pick),Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun onSubmitPhoto(view: View) {
        if (mAuth.currentUser == null) {
            Toast.makeText(requireActivity(),getString(R.string.prompt_please_login),Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(requireActivity(),getString(R.string.prompt_write_permission),Toast.LENGTH_SHORT)
                .show()
            return
        }

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val bitmap = (binding.imageviewPhoto.drawable as BitmapDrawable).bitmap
            val imageTitle = SimpleDateFormat("yyyyMMddHHmm").format(Date())
            val imagePath = saveToExternalStorage(imageTitle, bitmap)
//            val imagePath = MediaStore.Images.Media.insertImage(
//                requireActivity().contentResolver,
//                bitmap,
//                imageTitle,
//                ""
//            )

            val userEmail = mAuth.currentUser!!.email!!
            val gtphoto = GTPhoto(location.latitude,location.longitude,imagePath)
            val database = Firebase.database
            val mRef = database.getReference(getString(R.string.firebase_base_path))
            val userRef = mRef.child(userEmail.replace('.','-'))
            userRef.push().setValue(gtphoto)

            Toast.makeText(requireActivity(),getString(R.string.prompt_submission_success),Toast.LENGTH_SHORT)
                .show()
            Thread.sleep(2_000)
            findNavController().navigate(R.id.action_uploadPhotoFragment_to_googleMapFragment)
        }
    }

    private fun saveToExternalStorage(title: String, bitmap: Bitmap): String {
        var imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val resolver = requireActivity().contentResolver
        val contentValue = ContentValues()
        contentValue.put(MediaStore.Images.Media.DISPLAY_NAME, "$title.jpg")
        contentValue.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val imageUri = resolver.insert(imageCollection, contentValue)

        val outputStream = resolver.openOutputStream(imageUri!!)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        Log.d("saveToExternalStorage","image saved, url: ${imageUri.toString()}")
        return imageUri.toString()
    }


}