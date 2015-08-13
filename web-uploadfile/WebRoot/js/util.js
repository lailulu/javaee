var ColorHex=new Array('00','33','66','99','CC','FF');
var SpColorHex=new Array('FF0000','00FF00','0000FF','FFFF00','00FFFF','FF00FF');
var current=null;
function intocolor()
{
var colorTable=''
for (i=0;i<2;i++)
{
for (j=0;j<6;j++)
   {
    colorTable=colorTable+'<tr height=12>'
    colorTable=colorTable+'<td width=11 style="background-color:#000000">'
    
    if (i==0){
    colorTable=colorTable+'<td width=11 style="background-color:#'+ColorHex[j]+ColorHex[j]+ColorHex[j]+'">'} 
    else{
    colorTable=colorTable+'<td width=11 style="background-color:#'+SpColorHex[j]+'">'}

    
    colorTable=colorTable+'<td width=11 style="background-color:#000000">'
    for (k=0;k<3;k++)
     {
       for (l=0;l<6;l++)
       {
        colorTable=colorTable+'<td width=11 style="background-color:#'+ColorHex[k+i*3]+ColorHex[l]+ColorHex[j]+'">'
       }
     }
}
}
colorTable='<table width=253 border="0" cellspacing="0" cellpadding="0" style="border:1px #000000 solid;border-bottom:none;border-collapse: collapse" bordercolor="000000">'
           +'<tr height=30><td colspan=21 bgcolor=#cccccc>'
           +'<table cellpadding="0" cellspacing="1" border="0" style="border-collapse: collapse">'
           +'<tr><td width="3"><td><input type="text" name="DisColor" id="DisColor" size="6" disabled style="border:solid 1px #000000;background-color:#ffff00"></td>'
           +'<td width="3"><td><input type="text" name="HexColor" id="HexColor" size="7" style="border:inset 1px;font-family:Arial;" value="#000000"></td></tr></table></td></table>'
           +'<table border="1" cellspacing="0" cellpadding="0" style="border-collapse: collapse" bordercolor="000000" onmouseover="doOver(event)" onmouseout="doOut(event)" onclick="doclick(event)" style="cursor:hand;">'
           +colorTable+'</table>';          
document.getElementById("colorpanel").innerHTML=colorTable;
}



//����ɫֵ��ĸ��д
function doOver(earg) {
	  var e = earg||window.event;
	  var srcElement = e.srcElement?e.srcElement:e.target;
      if ((srcElement.tagName.toLowerCase() == "td") && (current!=srcElement)) {
        if (current!=null){
			$(current).attr("style","background-color:"+current._background);
		}    
        srcElement._background = $(srcElement).css("background-color");
        $("#DisColor").attr("style","background-color:"+$(srcElement).css("background-color"));
        $("#HexColor").val(RGBStr2Color($(srcElement).css("background-color").toUpperCase()));
        $(srcElement).attr("style","background-color:white");
        current = srcElement;
      }
}


//����ɫֵ��ĸ��д
function doOut(earg) {
    if (current!=null) $(current).attr("style","background-color:"+current._background.toUpperCase());
}

function ToHex(n){
 /* var h, l;
  n = Math.round(n);
  l = n % 16;
  h = Math.floor((n / 16)) % 16;
  return (hexch[h] + hexch[l]);*/
  var r = parseInt(n).toString(16);
  return r<10?'0'+r:r;
}

function RGB2Color(r, g, b){ 
  var r, g, b;
  return ( '#' + ToHex(r) + ToHex(g) + ToHex(b));
}

function RGBStr2Color(str){ 
  if(str.indexOf("RGB")!=-1||str.indexOf("rgb")!=-1){
	  var g = str.substring(str.indexOf(",")+1);
	  var b = g.substring(g.indexOf(",")+1,g.length-1);
	  str = RGB2Color(str.substring(4,str.indexOf(",")),g.substring(0,g.indexOf(",")),b);
  }
  return str;
}

function doclick(earg)
{   var e = earg||window.event;
	var srcElement = e.srcElement?e.srcElement:e.target;
    if (srcElement.tagName == "TD")
    {
        var clr = srcElement._background;
		clr = RGBStr2Color(clr);
        clr = clr.toUpperCase(); //����ɫֵ��д
        if (targetElement)
        {
            //��Ŀ���޼�������ɫֵ
            //targetElement.value = clr;
			$("#colorbox").val(clr);
			$("#selectcolor").attr("style","background-color:"+clr);
			//bug修改
			$("#colorinput").val(clr);
        }
        DisplayClrDlg(e,false);
        return clr;
    }
}

var targetElement = null; //������ɫ�Ի��򷵻�ֵ��Ԫ��

//���������ʱ��ȷ����ʾ����������ɫ�Ի���
//�����ɫ�Ի���������������ʱ���öԻ�������
//�����ɫ�Ի���ɫ��ʱ���� doclick ����4���ضԻ���
function OnDocumentClick(earg)
{
	var e = earg||window.event;
    var srcElement = e.srcElement?e.srcElement:e.target;
    if (srcElement.alt == "clrDlg")
    {
        //��ʾ��ɫ�Ի���
        targetElement = e.srcElement?e.srcElement:e.target;
        DisplayClrDlg(e,true);
    }
    else
    {
        //�Ƿ�������ɫ�Ի����ϵ���
        while (srcElement && srcElement.id!="colorpanel")
        {
            srcElement = srcElement.parentElement;
        }
        if (!srcElement)
        {
            //��������ɫ�Ի����ϵ���
            DisplayClrDlg(e,false);
        }
    }
    
}

//��ʾ��ɫ�Ի���
//display ����ʾ��������
//�Զ�ȷ����ʾλ��
function DisplayClrDlg(e,display)
{
    var clrPanel = document.getElementById("colorpanel");
    if (display)
    {
        var left = document.body.scrollLeft + e.clientX;
        var top = document.body.scrollTop + e.clientY;
		if(navigator.userAgent.toLowerCase().indexOf("firefox") > -1 ){ 
			//left += $("#colorpanel").width();
		}
        if (e.clientX+$("#colorpanel").width() > document.body.clientWidth)
        {
			left -= $("#colorpanel").width();
        }
        if (e.clientY+$("#colorpanel").height() > document.body.clientHeight)
        {
            top -= $("#colorpanel").height();
        }
	   $("#colorpanel").css("left",left+"px");
	   $("#colorpanel").css("top",top+"px");
       $("#colorpanel").show();
    }
    else
    {
       $("#colorpanel").hide();
    }
}

//////////////////////////�������������///////////////////////
//��ñ�����
var _table=document.getElementById("colortable");
function moveUp(_a,groupid){
 //ͨ��t�Ӷ����ȡ����е�����
 var _row=_a.parentNode.parentNode;
 var index = parseInt(_row.rowIndex);
 //����ǵ�һ�У�������һ�н���˳��
 if(_row.previousSibling.previousSibling){swapNode(_row,_row.previousSibling);
 	BasicInfoDwr.modifyGroupOrder("up",groupid,index,setOrderOpt);
	}
}
//ʹ��������ƣ����ղ���Ϊt�Ӷ���
function moveDown(_a,groupid){
 //ͨ��t�Ӷ����ȡ����е�����
 var _row=_a.parentNode.parentNode;
 var index = parseInt(_row.rowIndex);
 //��������һ�У�������һ�н���˳��
 if(_row.nextSibling){swapNode(_row,_row.nextSibling);
 	BasicInfoDwr.modifyGroupOrder("down",groupid,index,setOrderOpt);
	}
}
function setOrderOpt(){
	
}
//����ͨ�õĺ���}�����λ��
function swapNode(node1,node2){
 //��ȡ�����
 var _parent=node1.parentNode;
 //��ȡ}��������λ��
 var _t1=node1.nextSibling;
 var _t2=node2.nextSibling;
 //��node2���뵽ԭ4node1��λ��
 if(_t1)_parent.insertBefore(node2,_t1);
 else _parent.appendChild(node2);
 //��node1���뵽ԭ4node2��λ��
 if(_t2)_parent.insertBefore(node1,_t2);
 else _parent.appendChild(node1);
}
//����table��tr�ı�����ɫ
function setOtherTrColor(beginid,selectid){
	var num = document.getElementById('colortable').getElementsByTagName("tr").length;
	for(var i=beginid; i<num; i++){
		if((i-beginid)%2==0&&i!=selectid){
			document.getElementById('colortable').rows[i].className = "";
		}else if(i!=selectid){
			document.getElementById('colortable').rows[i].className = "alt";
		}
	}
}

function $checkNull(str)
{	
	if(str==null)
		return true;
	str = str.trim();
	if(str.length == 0)
		return true;
	
	return false;
}


function $checkInteger(str)     
{
	if (str.length != 0)
	{
		reg=/^[-+]?\d*$/;
		if (reg.test(str))
		{
			return true;
		}
	}

	return false;
}
function $getTimeString(time){	
	return time.getFullYear()+"-"+$formatData(time.getMonth()+1)+"-"+$formatData(time.getDate())+" "+$formatData(time.getHours())+":"+$formatData(time.getMinutes())+":"+$formatData(time.getSeconds());
}
function $formatData(str){
	var temp = parseInt(str);
	if(temp<10)
		return "0"+temp;
	return temp;
}

function $trim(str){ //删除左右两端的空格
 return str.replace(/(^\s*)|(\s*$)/g, "");
}