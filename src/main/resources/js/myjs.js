function koshin(){
  var hiduke　=　new Date();
  var year = hiduke.getFullYear();
  var month = hiduke.getMonth() + 1;
  var week = hiduke.getDay();
  var day = hiduke.getDate();
  var yobi = new Array("日","月","火","水","木","金","土");

  var str = "今日は"+year+"年"+month+"月"+day+"日"+yobi[week]+"曜日です。";
  document.write(str);
}
