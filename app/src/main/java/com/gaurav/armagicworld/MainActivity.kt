package com.gaurav.armagicworld

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.view.*
import android.widget.*
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper

class MainActivity : Activity() {
 private val names = arrayOf("❄️ Ice Age","🦖 Dinosaur Jungle","👻 Haunted World","👽 Alien Planet","🐉 Fantasy World")
 private val icons = arrayOf("🦣","🦖","👻","👽","🐉")
 private var selected=0
 private lateinit var root: FrameLayout
 private lateinit var texture: TextureView
 private lateinit var overlay: WorldOverlay
 private var camera: CameraDevice?=null
 private var session: CameraCaptureSession?=null
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState)
  root=FrameLayout(this);setContentView(root)
  texture=TextureView(this);root.addView(texture,FrameLayout.LayoutParams(-1,-1))
  overlay=WorldOverlay();root.addView(overlay,FrameLayout.LayoutParams(-1,-1))
  val panel=LinearLayout(this);panel.orientation=LinearLayout.VERTICAL;panel.setPadding(18,26,18,15)
  panel.setBackgroundColor(Color.argb(190,7,19,40))
  val title=TextView(this);title.text="🌌 AR Magic World";title.textSize=23f;title.setTextColor(Color.WHITE);panel.addView(title)
  val spinner=Spinner(this);val adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,names);spinner.adapter=adapter;panel.addView(spinner)
  spinner.onItemSelectedListener=object:android.widget.AdapterView.OnItemSelectedListener{
   override fun onNothingSelected(parent:android.widget.AdapterView<*>?){}
   override fun onItemSelected(parent:android.widget.AdapterView<*>?,view:View?,position:Int,id:Long){selected=position;overlay.invalidate()}
  }
  val hint=TextView(this);hint.text="कैमरे में जगह चुनने के लिए स्क्रीन पर टैप करो • Demo overlay";hint.setTextColor(Color.WHITE);panel.addView(hint)
  root.addView(panel,FrameLayout.LayoutParams(-1,-2,Gravity.TOP))
  overlay.setOnTouchListener { _,event->if(event.action==MotionEvent.ACTION_DOWN){overlay.place(event.x,event.y);true}else true }
  if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)requestPermissions(arrayOf(Manifest.permission.CAMERA),4) else startCamera()
 }
 override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<out String>,grantResults:IntArray){super.onRequestPermissionsResult(requestCode,permissions,grantResults);if(grantResults.firstOrNull()==PackageManager.PERMISSION_GRANTED)startCamera()else Toast.makeText(this,"Camera permission जरूरी है",Toast.LENGTH_LONG).show()}
 private fun startCamera(){texture.surfaceTextureListener=object:TextureView.SurfaceTextureListener{
  override fun onSurfaceTextureAvailable(surface:SurfaceTexture,w:Int,h:Int){openCamera()}
  override fun onSurfaceTextureSizeChanged(surface:SurfaceTexture,w:Int,h:Int){}
  override fun onSurfaceTextureDestroyed(surface:SurfaceTexture):Boolean{closeCamera();return true}
  override fun onSurfaceTextureUpdated(surface:SurfaceTexture){}
 };if(texture.isAvailable)openCamera()}
 private fun openCamera(){try{
  val manager=getSystemService(CAMERA_SERVICE) as CameraManager
  val id=manager.cameraIdList.firstOrNull { manager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING)==CameraCharacteristics.LENS_FACING_BACK }?:return
  if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)return
  manager.openCamera(id,object:CameraDevice.StateCallback(){
   override fun onOpened(device:CameraDevice){camera=device;try{
    val surface=Surface(texture.surfaceTexture)
    val request=device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);request.addTarget(surface)
    device.createCaptureSession(listOf(surface),object:CameraCaptureSession.StateCallback(){
     override fun onConfigured(s:CameraCaptureSession){session=s;try{s.setRepeatingRequest(request.build(),null,null)}catch(_:Exception){}}
     override fun onConfigureFailed(s:CameraCaptureSession){}
    },null)
   }catch(_:Exception){}}
   override fun onDisconnected(device:CameraDevice){device.close()}
   override fun onError(device:CameraDevice,error:Int){device.close()}
  },null)
 }catch(e:Exception){Toast.makeText(this,"Camera error: "+e.message,Toast.LENGTH_LONG).show()}}
 private fun closeCamera(){session?.close();camera?.close();session=null;camera=null}
 override fun onPause(){super.onPause();closeCamera()}
 override fun onResume(){super.onResume();if(::texture.isInitialized&&texture.isAvailable&&checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)openCamera()}
 inner class WorldOverlay:View(this){
  private val paint=Paint(3);private var px=-1f;private var py=-1f;private val started=System.currentTimeMillis()
  fun place(x:Float,y:Float){px=x;py=y;invalidate()}
  override fun onDraw(canvas:Canvas){super.onDraw(canvas);if(px<0)return
   val t=(System.currentTimeMillis()-started)/1000f
   paint.color=when(selected){0->Color.rgb(170,224,255);1->Color.rgb(74,180,82);2->Color.rgb(143,105,181);3->Color.rgb(95,211,216);else->Color.rgb(240,177,83)}
   paint.alpha=115;canvas.drawOval(px-160,py-50,px+160,py+70,paint);paint.alpha=255
   paint.textAlign=Paint.Align.CENTER;paint.textSize=65f
   for(i in 0..2){val x=px+(i-1)*95+Math.sin((t+i).toDouble()).toFloat()*22;val y=py-65+Math.cos((t*2+i).toDouble()).toFloat()*9;canvas.drawText(icons[selected],x,y,paint)}
   paint.textSize=28f;paint.color=Color.WHITE;canvas.drawText(names[selected],px,py+100,paint)
   postInvalidateDelayed(45)
  }
 }
}