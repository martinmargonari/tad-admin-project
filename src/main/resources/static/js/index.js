$(document).ready(function() {
  
  var animating = false,
      submitPhase1 = 3000,
      submitPhase2 = 400,
      logoutPhase1 = 800,
      $login = $(".login"),
      $app = $(".app"),
      $login__error = $(".login__error");
  
  function ripple(elem, e) {
    $(".ripple").remove();
    var elTop = elem.offset().top,
        elLeft = elem.offset().left,
        x = e.pageX - elLeft,
        y = e.pageY - elTop;
    var $ripple = $("<div class='ripple'></div>");
    $ripple.css({top: y, left: x});
    elem.append($ripple);
  };

  function  encrypt(){
    var salt = CryptoJS.lib.WordArray.random(128/8);
    var iv = CryptoJS.lib.WordArray.random(128/8);
    console.log('salt  '+ salt );
    console.log('iv  '+ iv );
    var password = document.getElementById("password").value;
    var key128Bits = CryptoJS.PBKDF2("Secret Passphrase", salt, { keySize: 128/32 });
    console.log( 'key128Bits '+ key128Bits);
    var key128Bits100Iterations = CryptoJS.PBKDF2("Secret Passphrase", salt, { keySize: 128/32, iterations: 100 });
    console.log( 'key128Bits100Iterations '+ key128Bits100Iterations);
    var encrypted = CryptoJS.AES.encrypt(password, key128Bits100Iterations, { iv: iv, mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7  });
    console.log('encrypted   '+ encrypted  );
    var encryptedPassword = document.getElementById("encryptedPassword"); encryptedPassword.value = encrypted;
    var saltText = document.getElementById("salt"); saltText.value = salt;
    var ivText = document.getElementById("iv"); ivText.value = iv;
  }
  
  $(document).on("click", ".login__submit", function(e) {
    if (animating) return;
    animating = true;
    var that = this;
    encrypt();
    ripple($(that), e);
    $(that).addClass("processing");
    setTimeout(function() {
      $(that).addClass("success");
      setTimeout(function() {
      }, submitPhase2 - 70);
      setTimeout(function() {
        animating = false;
        $(that).removeClass("success processing");
      }, submitPhase2);
    }, submitPhase1);
  });
  
  $(document).on("click", ".app__logout", function(e) {
    if (animating) return;
    $(".ripple").remove();
    animating = true;
    var that = this;
    $(that).addClass("clicked");
    setTimeout(function() {
      $app.removeClass("active");
      $login.show();
      $login.css("top");
      $login.removeClass("inactive");
    }, logoutPhase1 - 120);
    setTimeout(function() {
      $app.hide();
      animating = false;
      $(that).removeClass("clicked");
    }, logoutPhase1);
  });
  
});