<%@ page language="java" contentType="text/html;charset=utf-8" import="itims.share.right.bean.User"%>
<%@ include file="/share/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
User user = (User)session.getAttribute("loginUserID");
String userid = user.getUserID();
String domain = user.getDmsn();
%>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link rel="stylesheet" type="text/css" href="../util.css" />
	<link rel="stylesheet" type="text/css" href="upload/uploadify.css" />
	<script type="text/javascript" src="${pageContext.request.contextPath}/share/json2.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/share/itims-share.js"></script>
	<script type='text/javascript' src='${pageContext.request.contextPath}/dwr/engine.js'></script>
	<script type='text/javascript' src='${pageContext.request.contextPath}/dwr/util.js'></script> 
	<script type='text/javascript' src='${pageContext.request.contextPath}/dwr/interface/BasicInfoDwr.js'></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/widget/jquery/jquery-1.7.2.js"></script>
	<%@ include file="/frmwk/tree/custTreeCommon.jsp" %>
	<script type="text/javascript" src="js/jquery.jplayer.min.js"></script>
	<script type="text/javascript" src="upload/swfobject.js"></script>
	<script type="text/javascript" src="upload/jquery.uploadify.v2.1.4.min.js"></script>
	<script type='text/javascript' src='js/util.js'></script> 
	<%@include file="../../ui/theme/include.jsp"%>
	<title>无标题文档</title>
</head>
<script language="javascript">
var array=["","Emerg","Alert","Crit","Error","Warning","Notice","Info","Debug"];
var currentNodeid = "2015";//上海中电投定制   写死即可
var soundPath = "";
var soundCfgMap = null;
var basePath = '<%=basePath%>';
var dmsn= "<%=domain%>";
$(function(){
	
	BasicInfoDwr.getUdpSoundCfg(initSoundCfg);
	
	//var total = getWindowSize().height;
	//$("#group").height(total-20);
	
	$("#soundtable td").attr("align","center");
	
	$(".std").bind("click",function(){
		var id = $(this).attr("id").substr(3);
		var sound = $(this).attr("cfg");
		var X = $(this).offset().top;
		var Y = $(this).offset().left+32;
		
		$("#contextMemGroupMenu").css("left",Y).css("top",X).show();
		$("#contextMemGroupMenu li").unbind("click");
		$("#setbutton").bind("click",function(){ 
			setSound(sound,id);
		});
		
		if(sound==undefined ||sound==""){
			$(".rightAttr").unbind("click");
			$(".rightAttr").hide();
		}else
		{
			$(".rightAttr").show();
			$("#delbutton").bind("click",function(){ 
				delSound(sound,id);
			}); 
			$("#listenbutton").bind("click",function(){ 
				listenSound(sound,id);
			});  
		}
	});
	
	
    $("#jquery_jplayer_1").jPlayer( {
	    supplied: "mp3,wav*",
	    swfPath: "js"
    });
	 
	 $("#uploadify").uploadify({
        'uploader': 'upload/uploadify.swf',
        'script': '${pageContext.request.contextPath}/SyslogUploadServlet',
		'scriptData': {'desc':''},
        'cancelImg': 'upload/cancel.png',
        'queueID': 'fileQueue', //和存放队列的DIV的id一致
		'method':'get',
        'auto': false, //是否自动开始 
        'multi': false, //是否支持多文件上传 
        //'folder':'/viewframework/img/backgroundIMG/',
        'buttonImg':'${pageContext.request.contextPath}/syslog/event/eventImg/openSound.png',
        'width':'60',
        'height':'14',
        'wmode':'transparent',
      //'simUploadLimit': 3, //一次同步上传的文件数目 
        'sizeLimit': 10240000, //设置单个文件大小限制 19871202
      //'queueSizeLimit': 2, //队列中同时存在的文件个数限制 
        'fileDesc': '支持格式:mp3', //如果配置了以下的'fileExt'属性，那么这个属性是必须的 支持格式:wav/mp3/rmvb/wma/.
        'fileExt': '**.mp3',//允许的格式*.wav;*.mp3;*.rmvb;*.wma;*  

        'onSelect':function (event, queueID, fileObj){ 
				if(fileObj.size>4*1024*1024){
					alert("您选择的文件大小超过4M！请重新选择！");return;
					
				}
				$("#sound").val(fileObj.name);
				needUpload = true;
			} ,
		'onComplete':function(event, queueID, fileObj, response, data) {
				 setOpt(response);
            }

    });
	 
	$("#saveButton").bind("click",function(){
		if($("#levelname").val()==null||$("#levelname").val().length==0){
			alert("请选择您要设置声音的级别！");return;
		}
		
		if($("#sound").val()==null||$("#sound").val().length==0||!needUpload){
			alert("请选择声音文件！");return;
		}
		
		javascript:$("#uploadify").uploadifySettings('scriptData',{'fgroupid':$("#groupid").val(),'colname':$("#colname").val(),'desc': $("#desc").val(),'domain':dmsn}); 
		javascript:$("#uploadify").uploadifyUpload();
		$("#tishi").css("display","");
		needUpload = false;
	});
	 
	 
	$("ul.sContextMenu li").bind("mouseover",function(){
		$(this).addClass("highlight");
	}).bind("mouseout",function(){
		$(this).removeClass("highlight");
	});
	
	$("ul.sContextMenu").css("width",$("ul.sContextMenu").innerWidth())
		.bind("click",clearAllDropdown)
		.bind("mouseout",delayClearDropdown)
		.bind("mouseover",cancelDelayClear);
	
});

//加载配置信息并缓存
function initSoundCfg(data){
	if(data !=null && data!=""&& data!="{}"){
		var json = JSON.parse(data);
		soundPath = json.soundPath;
		soundCfgMap= json;
		$("#groupid").val(currentNodeid);
		$(".std").attr("src","eventImg/Unavailable.png");
		$(".std").attr("cfg","");
		initSound(currentNodeid);
	}
}

//加载配置信息 
function initSound(nodeid){
	var cfg = soundCfgMap[nodeid];
	if(cfg!=undefined && cfg!=null && cfg!=''){
		$(".std").each(function(){
			var id = $(this).attr("id").substr(3);
			var temp = cfg["flevel"+id];
			if(temp!=undefined && temp!=null && temp!=''){
				$(this).attr("src","eventImg/Sound.png");
				$(this).attr("cfg",temp);
			}
		});
	}
}

//设置事件
function setSound(cfg,level){
	needUpload = false;
	$("#colname").val("flevel"+level);
	$("#levelname").val(array[level]);
	if(cfg==null||cfg=="null"||cfg==""){
		$("#sound").val("");
		$("#desc").val("");
	}else{
		$("#sound").val(cfg.split("&&||&&")[1]);
		$("#desc").val(cfg.split("&&||&&")[0]);
	}
}

//删除事件
function delSound(cfg,level){
	if(confirm("您确定要删除该配置信息吗？")){
		BasicInfoDwr.modifySoundCfg("{\"fgroupid\":\""+currentNodeid+"\",\"colname\":\"flevel"+level+"\",\"sound\":\""+cfg+"\",\"remark\":\"1\"}",null,setOpt);
	}
}

//删除回调
function setOpt(obj){
	obj = JSON.parse(obj);
	$("#tishi").css("display","none");
	if(obj.success=="true"){
		alert(obj.mesg);
		BasicInfoDwr.getUdpSoundCfg(initSoundCfg);
	}else{
		alert("操作失败！");
	}
}

//试听声音
function listenSound(cfg,level){
	$("#colname").val("flevel"+level);
	$("#levelname").val(array[level]);
	if(cfg==null||cfg=="null"||cfg==""){
		$("#sound").val("");
		$("#desc").val("");
		alert("还未设置声音文件！");
	}else{
		$("#sound").val(cfg.split("&&||&&")[2]);
		$("#desc").val(cfg.split("&&||&&")[0]);
		BasicInfoDwr.listenSoundCfg(cfg.split("&&||&&")[1], setListenSoundCfg);
	}
}

//试听声音回调
function setListenSoundCfg(obj){
	if(obj!="false"){
		playSound(soundPath+obj);
	}else{
		alert("未获取到声音文件！");
	}
}
function playSound(fileName){
	showPlayer(basePath+"syslog/event"+fileName);
}
function showPlayer(url){
	 url = url.replace(/\\/g,"/");
	 $("#jquery_jplayer_1").jPlayer("pauseOthers");
	 $("#jquery_jplayer_1").jPlayer("setMedia", {
        mp3: url // Defines the m4v url
     }).jPlayer("play");
}
function clearAllDropdown(){
	$("ul.sContextMenu").hide();
}
var clearDropdownHandle=null;
function delayClearDropdown(){
	cancelDelayClear();
	clearDropdownHandle=setTimeout("clearAllDropdown()",500);
}
function cancelDelayClear(){
	clearTimeout(clearDropdownHandle);
}
</script>
<body class="sTransBG0"> 
<div id="tablediv" class="mainContent">
	<div style="height:100%;margin:0px 10px 0px 10px">
		<table width='100%' id='soundtable' class='simple'>
			<tr><th width="50%">级别</th><th width="50%">声音</th></tr>
			<tr>
				<td >Emerg</td>
				<td><img id="img1" class="std"  src="eventImg/Unavailable.png" height="32" width="32" border="0" style='cursor:pointer'/></td>
			</tr>
			<tr class='alt'>
				<td>Alert</td>
				<td><img id="img2" class="std"  src="eventImg/Unavailable.png" height="32" width="32" border="0" style='cursor:pointer'/></td>
			</tr>
			<tr>
				<td>Crit</td>
				<td><img id="img3" class="std"  src="eventImg/Unavailable.png" height="32" width="32" border="0" style='cursor:pointer'/></td>
			</tr>
			<tr class='alt'>
				<td>Error</td>
				<td><img id="img4" class="std"  src="eventImg/Unavailable.png" height="32" width="32" border="0" style='cursor:pointer'/></td>
			</tr>
			<tr>
				<td>Warning</td>
				<td><img id="img5" class="std"  src="eventImg/Unavailable.png" height="32" width="32" border="0" style='cursor:pointer'/></td>
			</tr>
			<tr class='alt'>
				<td>Notice</td>
				<td><img id="img6" class="std"  src="eventImg/Unavailable.png" height="32" width="32" border="0" style='cursor:pointer'/></td>
			</tr>
			<tr>
				<td>Info</td>
				<td><img id="img7" class="std"  src="eventImg/Unavailable.png" height="32" width="32" border="0" style='cursor:pointer'/></td>
			</tr>
			<tr class='alt'>
				<td>Debug</td>
				<td><img id="img8" class="std"  src="eventImg/Unavailable.png" height="32" width="32" border="0" style='cursor:pointer'/></td>
			</tr>
		</table>
	</div>
	<div style="height:100%;margin:10px 10px 0px 10px">
		<form enctype="multipart/form-data" action="" method="post" id="basicform" class="blue">
			<input type="hidden" id="groupid" />
			<input type="hidden" id="colname" />
		
			<h2>syslog声音信息配置</h2>
			<!-- <label>分组：</label><input name="fname" type="text" id="fname"  disabled="disabled"/><br/> -->
			<label>级别：</label><input name="levelname" type="text" id="levelname"  disabled="disabled"/><br/>
			<label>声音文件：</label><span id="sounddiv" style="display:inline-block;"><input name="sound" type="text" id="sound"   disabled="disabled" />
			<input type="file" name="uploadify" id="uploadify" style="height:30"/></span>
			<br/>
			<label>描述：</label><input type="text" id="desc" /> 
		   	<br/>
			<div class="buttonarea"><div id="tishi" style="display:none">文件上传中，请稍候。。。<img src="eventImg/wait.gif"/></div>
		    <input id="saveButton" name="Submit" type="button" class="sButton2" value="保存"  /></div>
		</form>
	</div>
	<ul class="sContextMenu" style="width:100px;display:none" id="contextMemGroupMenu">
	  <li id="setbutton"><img src="eventImg/modify.png" />设置</li>
	  <li id="delbutton" class="rightAttr" ><img src="/itims/img/general/delete.png" />删除</li>
	  <li id="listenbutton" class="rightAttr"  ><img src="eventImg/soundManage.png" />试听</li>
	</ul>
	<!-- 开始控制声音播放 -->
	<div class="jp-cont">
	   <div id="jquery_jplayer_1" class="jp-jplayer"></div>
		<a href="#" id="unmute" class="volumn_btn unmute_on"></a>
		<a href="#" id="mute" class="volumn_btn mute_off"></a>
	</div>	
</div>
</body>
</html>