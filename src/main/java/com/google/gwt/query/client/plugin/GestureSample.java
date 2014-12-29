package com.google.gwt.query.client.plugin;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.$$;
import static com.google.gwt.query.client.GQuery.document;
import static com.google.gwt.query.client.GQuery.lazy;
import static com.google.gwt.query.client.GQuery.window;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.plugin.GestureObjects.Options;
import com.google.gwt.query.client.plugin.KeyFrame.Frame;
import com.google.gwt.query.client.plugin.KeyFrame.FrameGenerator;
import com.google.gwt.query.client.plugins.effects.Transitions;
import com.google.gwt.user.client.Random;

/**
 */
public class GestureSample implements EntryPoint {
  Properties main_css = $$("background:#F6EBEB, position:absolute, bottom:0px, right:0px, top:0px, left:0px, overflow: hidden");
  Properties img_css = $$("background-image: url('" + GWT.getModuleBaseForStaticFiles() + "img.jpg'), background-size: contain, background-repeat: no-repeat, background-position: center");
  Properties div_css = $$("margin-left: 0px, margin-top: 0px, rotateX:0deg, rotate:0deg, rotateY:0deg, rotateZ:0deg, position: absolute, width: 50%, height: 50%, top: 20%, left: 25%");
  Properties par_css = $$("position: absolute, background: black, color:white, padding: 6px; font-family: arial, font-size: 16px; position: absolute, bottom: 10px, left: 10px, right: 10px, border-radius: 6px, text-align: center");
  Properties ball = $$("position: absolute, background: red, border-radius: 50px, width: 15px, height: 15px, top: 10px");

  GQuery c = $("<div></div>").css(main_css).appendTo(document);
  Transitions d = $("<div></div>").as(Transitions.Transitions).css(div_css).css(img_css).appendTo(c).as(Transitions.Transitions);
  GQuery s = $("<p id='msg'>Try a gesture </p>").css(par_css).appendTo(c);
  
  String shakeAnimation = $(window).as(KeyFrame.KeyFrame).keyFrame(null, $$("duration: 1s, count: infinite"), new FrameGenerator() {
    int r () {
      return 3 * Random.nextInt(3) * (Random.nextBoolean() ? -1 : 1);
    }
    public void f(Frame f) {
      if (f.percent() % 10 == 0)
        f.transform($$().set("translateX", r()).set("translateY", r()).set("rotate", r()));
    }
  });
  
  
  void log(String m) {
    // console.log(m);
    s.text(m);
  }
  
  public void onModuleLoad() {
    // Mobile devices
    if (Gesture.hasGestures) {
      // The very first time we use Gesture plugin special events are loaded as well
      $(window).as(Gesture.Gesture)
      // Set the viewPort so as the user cannot scale the app
      .viewPortDefault()
      // Go full-screen the first time the user touches the screen
      .fullScreen();
    }
    // Desktop devices
    else {
      // Load all special events so we don't need to call GQuery.as anymore
      Gesture.load();
    }
    
    // Draw number of touches on the screen
    drawTouches();

    // We are notified when the screen orientation changes
    listenOrientiationEvents();
    listenShakeEvents();
    listenTapEvents();
    listenSwipeEvents();
    listenGestureEvents();
  }
  
  private void drawTouches() {
    c.bind("jGestures.touchstart", new Function() {
      public void f() {
        $(".fingers").remove();
        Integer fingers = arguments(1);
        for (; fingers> 0; fingers --) {
          $("<div>").addClass("fingers").css("right", (fingers * 20) + "px").css(ball).appendTo(c);
        }
      }
    });
    c.bind("jGestures.touchend;processed", new Function() {
      public void f() {
        $(".fingers").fadeOut(1000);
      }
    });
  }
  
  private void move(int moved, String dir) {
    if (dir != null && !dir.isEmpty() && moved > 1) {
      double top = d.cur("margin-top") + (dir.contains("up") ? -1 * moved : dir.contains("down") ? moved : 0);
      double left = d.cur("margin-left")  + (dir.contains("left") ? -1 * moved : dir.contains("right") ? moved : 0);
      d.css($$("margin-top:" + top + "px, margin-left:" + left + "px"));
    }
  }
  
  private void rotate(int deg) {
    d.css(div_css).animate($$("rotate:" + deg + "deg"));
  }
  private void swipe(int top, int left) {
    d.css("margin-top", top < 0 ? "70%" : top > 0 ? "-70%" : "0%");
    d.css("margin-left", left < 0 ? "75%" : left > 0 ? "-75%" : "0%");
    d.animate(div_css);
  }
  private void shake() {
    d.addClass(shakeAnimation).delay(1000, lazy().removeClass(shakeAnimation).done());
  }
  
  /**
   * Performance problems, because this is continuously being fired.
   */
  private void listenOrientiationEvents() {
    $(window).bind("orientationchange", new Function(){public void f(){
      Options o = arguments(0);
      // this could be null, if the device already supports orientationchange
      if (o != null) {
        log("ORIENTATION-CHANGE " + o.description());
        c.css("background-color", o.description().contains("landscape") ? "#F1BBBB" : "#88DBA6");
        swipe(100, 0);
      }
    }});
  }

  /**
   * Performance problems, because this is continuously being fired.
   */
  private void listenShakeEvents() {
    $(window).bind("shake", new Function(){public void f(){
      Options o = arguments(0);
      shake();
      log("SHAKE " + o.description());
    }});    
//    $(window).bind("shakefrontback", new Function(){public void f(){
//      Options o = arguments(0);
//      log("SHAKE-FRONT-BACK " + o.description());
//    }});    
//    $(window).bind("shakeleftright", new Function(){public void f(){
//      Options o = arguments(0);
//      log("SHAKE-LEFT-RIGHT " + o.description());
//    }});    
//    $(window).bind("shakeupdown", new Function(){public void f(){
//      Options o = arguments(0);
//      log("SHAKE-UP-DOWN " + o.description());
//    }});
  }

  private void listenGestureEvents() {
    // PINCH (only iOS)
    c.bind("pinch", new Function(){public void f(){
      Options o = arguments(0);
      log("PINCH " + o.description());
    }});    
    // ROTATE (only iOS)
    c.bind("rotate", new Function(){public void f(){
      Options o = arguments(0);
      log("ROTATE " + o.description() + " rotation=" + o.rotation());
    }});
  }
  
  private void listenSwipeEvents() {
    c.bind("swipemove", new Function(){public void f(){
      Options o = arguments(0);
      move((int)o.delta().get(0).moved(), o.directionName());
      log("SWIPEMOVE " + o.description());
    }});
    c.bind("swipetwo", new Function(){public void f(){
      Options o = arguments(0);
      log("SWIPE-TWO " + o.description());
    }});    
    c.bind("swipethree", new Function(){public void f(){
      Options o = arguments(0);
      log("SWIPE-THREE " + o.description());
    }});    
    c.bind("swipefour", new Function(){public void f(){
      Options o = arguments(0);
      log("SWIPE-FOUR " + o.description());
    }});    
    c.bind("swipeup", new Function(){public void f(){
      swipe(-100, 0);
      Options o = arguments(0);
      log("SWIPE-UP " + o.description());
    }});    
    c.bind("swiperightup", new Function(){public void f(){
      swipe(-100, 100);
      Options o = arguments(0);
      log("SWIPE-RIGHT-UP " + o.description());
    }});    
    c.bind("swiperight", new Function(){public void f(){
      swipe(0, 100);
      Options o = arguments(0);
      log("SWIPE-RIGHT " + o.description());
    }});    
    c.bind("swiperightdown", new Function(){public void f(){
      swipe(100, 100);
      Options o = arguments(0);
      log("SWIPE-RIGHT-DOWN " + o.description());
    }});    
    c.bind("swipedown", new Function(){public void f(){
      swipe(100, 0);
      Options o = arguments(0);
      log("SWIPE-DOWN " + o.description());
    }});    
    c.bind("swipeleftdown", new Function(){public void f(){
      swipe(100, -100);
      Options o = arguments(0);
      log("SWIPE-LEFT-DOWN " + o.description());
    }});    
    c.bind("swipeleft", new Function(){public void f(){
      swipe(0, -100);
      Options o = arguments(0);
      log("SWIPE-LEFT " + o.description());
    }});    
    c.bind("swipeleftup", new Function(){public void f(){
      swipe(-100, -100);
      Options o = arguments(0);
      log("SWIPE-LEFT-UP " + o.description());
    }});
  }

  private void listenTapEvents() {
    c.bind("tapone", new Function(){public void f() {
      rotate(360);
      Options o = arguments(0);
      log("TAP-ONE " + o.description() + " x="+  o.screenX() + " y="+  o.screenY());
    }});
    c.bind("taptwo", new Function(){public void f() {
      rotate(360 * 2);
      Options o = arguments(0);
      log("TAP-TWO " + o.description());
    }});
    c.bind("tapthree", new Function(){public void f() {
      rotate(360 * 3);
      Options o = arguments(0);
      log("TAP-THREE " + o.description());
    }});
    c.bind("tapfour", new Function(){public void f() {
      rotate(360 * 4);
      Options o = arguments(0);
      log("TAP-FOUR " + o.description());
    }});
    c.bind("mrotate", new Function(){public void f() {
      Options o = arguments(0);
      rotate(o.rotation() == 1 ? 90 : -90);
      log("TAP-ROTATE " + o.description());
    }});
  }
}
