package com.mridx.androidtemplate.presentation.dialog.bottom_message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mridx.androidtemplate.databinding.BottomMessageDialogBinding
import com.mridx.androidtemplate.presentation.base.fragment.rounded_bottomsheet_dialog.RoundedBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomMessageDialog : RoundedBottomSheetDialog() {


    class Builder {

        private val args = bundleOf()

        private var listener_: ((dialog: BottomSheetDialogFragment) -> Unit)? = null

        fun setMessage(message: String): Builder {
            args.putString("message", message)
            return this
        }

        fun setOnDone(listener: ((dialog: BottomSheetDialogFragment) -> Unit)): Builder {
            listener_ = listener
            return this
        }

        fun show(fragmentManager: FragmentManager, tag: String = "BottomMessageDialog") {
            BottomMessageDialog().apply {
                arguments = args
                onDoneListener = listener_
            }.show(fragmentManager, tag)
        }

    }


    private var binding_: BottomMessageDialogBinding? = null
    private val binding get() = binding_!!


    var onDoneListener: ((dialog: BottomSheetDialogFragment) -> Unit)? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = BottomMessageDialogBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        val message = arguments?.getString("message")

        binding.successMessageView.text = message

        binding.doneBtn.setOnClickListener {
            if (onDoneListener == null) {
                dismiss()
                return@setOnClickListener
            }
            onDoneListener!!.invoke(this)
        }

    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}