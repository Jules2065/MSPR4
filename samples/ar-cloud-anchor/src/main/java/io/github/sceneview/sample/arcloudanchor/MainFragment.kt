package io.github.sceneview.sample.arcloudanchor

import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Anchor
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.CursorNode
import io.github.sceneview.math.Position
import io.github.sceneview.utils.doOnApplyWindowInsets

class MainFragment : Fragment(R.layout.fragment_main) {

    lateinit var sceneView: ArSceneView
    lateinit var loadingView: View
    lateinit var anchorButton: ExtendedFloatingActionButton
    lateinit var returnBack: ExtendedFloatingActionButton

    lateinit var cursorNode: CursorNode
    lateinit var modelNode: ArModelNode

    var isLoading = false


    val fileName by lazy { "${requireContext().externalCacheDir?.absolutePath}/screen_record.mp4" }

    lateinit var recorder: MediaRecorder

    var isRecording = false
        set(value) {
            field = value
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = view.findViewById(R.id.loadingView)
        anchorButton = view.findViewById<ExtendedFloatingActionButton>(R.id.anchorButton).apply {
            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    systemBarsInsets.bottom + bottomMargin
            }
            setOnClickListener { cursorNode.createAnchor()?.let { anchorOrMove(it) } }
        }

        returnBack = view.findViewById<ExtendedFloatingActionButton>(R.id.returnBack).apply {
            setOnClickListener {
                activity?.finish()
            }
        }

        sceneView = view.findViewById<ArSceneView?>(R.id.sceneView).apply {
            planeRenderer.isVisible = false
            // Handle a fallback in case of non AR usage. The exception contains the failure reason
            // e.g. SecurityException in case of camera permission denied
            onArSessionFailed = { _: Exception ->
                // If AR is not available or the camara permission has been denied, we add the model
                // directly to the scene for a fallback 3D only usage
                modelNode.centerModel(origin = Position(x = 0.0f, y = 0.0f, z = 0.0f))
                modelNode.scaleModel(units = 1.0f)
                sceneView.addChild(modelNode)
            }
            onTapAr = { hitResult, _ ->
                anchorOrMove(hitResult.createAnchor())
            }
        }

        cursorNode = CursorNode().apply {
            onHitResult = { node, _ ->
                if (!isLoading) {
                }
            }
        }
        sceneView.addChild(cursorNode)

        modelNode = ArModelNode(
            modelGlbFileLocation = Activity.model3D,
            onLoaded = { modelInstance ->
                anchorButton.setIconResource(R.drawable.ic_target)
                isLoading = false
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
    }

    fun anchorOrMove(anchor: Anchor) {
        if (!sceneView.children.contains(modelNode)) {
            sceneView.addChild(modelNode)
        }
        modelNode.anchor = anchor
    }

    fun startRecording() {
        recorder = MediaRecorder().apply {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(fileName)
            setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)
            setVideoSize(sceneView.width, sceneView.height)
            prepare()
        }
        recorder.start()
        sceneView.startMirroring(recorder.surface)
    }

    private fun stopRecording() {
        /*if (recorder != null) {
            sceneView.stopMirroring(recorder.surface)
            recorder.stop()
            recorder.release()
        }*/
    }
}