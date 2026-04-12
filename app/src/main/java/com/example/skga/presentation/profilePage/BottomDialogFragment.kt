import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.skga.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialogFragment(val onSourceSelected: (Boolean) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_bottom_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnCamera).setOnClickListener {
            onSourceSelected(true)
            dismiss()
        }

        view.findViewById<Button>(R.id.btnGallery).setOnClickListener {
            onSourceSelected(false)
            dismiss()
        }
    }
}