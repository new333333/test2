function showhide1(div1)
{
	var element1;
	element1 = document.getElementById(div1);

  	if(element1.style.display == "none")
  	{
		element1.style.display = "block";
	}
	else
	{
		element1.style.display = "none";
	} 
}

function showhideInline(div1,a1)
{
	var element1;
	element1 = document.getElementById(div1);
	element2 = document.getElementById(a1);

  	if(element1.style.display == "none")
  	{
		element1.style.display = "inline";
		element2.style.color = "#000";
	}
	else
	{
		element1.style.display = "none";
		element2.style.color = "#1C5894"; //userapp link color
	} 
}

function showhide2(div1,div2)
{
	var element1 = document.getElementById(div1);
	var element2 = document.getElementById(div2);

  	if(element1.style.display == "none")
  	{
		element1.style.display = "block";
		element2.style.display = "none";		
	}

	else
	{
		element1.style.display = "none";
		element2.style.display = "block";
	} 
}

function show2hide2(div1,div2)
{
	var element1;
	var element2;
	element1 = document.getElementById(div1);
	element2 = document.getElementById(div2);

  	if(element1.style.display == "none")
  	{
		element1.style.display = "block";
		element2.style.display = "block";		
	}

	else
	{
		element1.style.display = "none";
		element2.style.display = "none";
	} 
}

function showhide(div1,div2)
{
	var element1 = document.getElementById(div1);	
	var element2 = document.getElementById(div2);

  	if(element1.style.display == "none")
  	{
		element1.style.display = "block";
		element2.style.display = "none";
	}
}

function navshowhide(divID, imageID)
{
	var element;
	element = document.getElementById(divID);
	
    	if(element.style.display == "none")
  	{
		element.style.display = "block";
		document.images[imageID].src = imageArray["minus"].src;
	}
	else
	{
		element.style.display = "none";
		document.images[imageID].src = imageArray["plus"].src;

	} 
}

function showhide3tabs(div1,div2,div3)
{
	var element1 = document.getElementById(div1);
	var element2 = document.getElementById(div2);
	var element3 = document.getElementById(div3);
	
  	if(element1.style.display == "none")
  	{
		element1.style.display = "block";
		element2.style.display = "none";		
		element3.style.display = "none";		
	}
}

function showhide3subtabs(div1,div2,div3)
{
	var tab1 = document.getElementById(div1);
	var tab2 = document.getElementById(div2);
	var tab3 = document.getElementById(div3);
	
  	if (document.images)
  	{
		tab1.style.fontSize = "1em";
		tab1.style.fontWeight = "bolder";
		tab1.style.backgroundImage = "url(../../common/images/bg/subtab_bg.gif)"; 
		tab1.style.backgroundRepeat = "repeat-x"; 
		tab1.style.backgroundPosition = "0 bottom"; 
		tab1.style.border= "1px solid #efeeec";

		tab2.style.fontSize = "0.9em";
		tab2.style.fontWeight = "normal";
		tab2.style.backgroundImage = "none"; 
		tab2.style.border= "";

		tab3.style.fontSize = "0.9em";
		tab3.style.fontWeight = "normal";
		tab3.style.backgroundImage = "none"; 
		tab3.style.border= "";
	}
}

function showhide4subtabs(div1,div2,div3,div4)
{
	var tab1 = document.getElementById(div1);
	var tab2 = document.getElementById(div2);
	var tab3 = document.getElementById(div3);
	var tab4 = document.getElementById(div4);
	
  	if (document.images)
  	{
		tab1.style.fontSize = "1em";
		tab1.style.fontWeight = "bolder";
		tab1.style.backgroundImage = "url(../../common/images/bg/subtab_bg.gif)"; 
		tab1.style.backgroundRepeat = "repeat-x"; 
		tab1.style.border= "1px solid #efeeec";
		tab1.style.backgroundPosition = "0 bottom"; 

		tab2.style.fontSize = "0.9em";
		tab2.style.fontWeight = "normal";
		tab2.style.backgroundImage = "none"; 
		tab2.style.border= "";

		tab3.style.fontSize = "0.9em";
		tab3.style.fontWeight = "normal";
		tab3.style.backgroundImage = "none"; 
		tab3.style.border= "";

		tab4.style.fontSize = "0.9em";
		tab4.style.fontWeight = "normal";
		tab4.style.backgroundImage = "none"; 
		tab4.style.border= "";
	}
}

function showhide4tabs(div1,div2,div3,div4)
{
	var element1 = document.getElementById(div1);
	var element2 = document.getElementById(div2);
	var element3 = document.getElementById(div3);
	var element4 = document.getElementById(div4);
	
  	if(element1.style.display == "none")
  	{
		element1.style.display = "block";
		element2.style.display = "none";		
		element3.style.display = "none";		
		element4.style.display = "none";		
	}
}

function showhide5tabs(div1,div2,div3,div4,div5)
{
	var element1 = document.getElementById(div1);
	var element2 = document.getElementById(div2);
	var element3 = document.getElementById(div3);
	var element4 = document.getElementById(div4);
	var element5 = document.getElementById(div5);
	
  	if(element1.style.display == "none")
  	{
		element1.style.display = "block";
		element2.style.display = "none";		
		element3.style.display = "none";		
		element4.style.display = "none";		
		element5.style.display = "none";		
	}
}
function showhide6tabs(div1,div2,div3,div4,div5,div6)
{
	var element1 = document.getElementById(div1);
	var element2 = document.getElementById(div2);
	var element3 = document.getElementById(div3);
	var element4 = document.getElementById(div4);
	var element5 = document.getElementById(div5);
	var element6 = document.getElementById(div6);
	
  	if(element1.style.display == "none")
  	{
		element1.style.display = "block";
		element2.style.display = "none";		
		element3.style.display = "none";		
		element4.style.display = "none";		
		element5.style.display = "none";		
		element6.style.display = "none";		
	}
}

function showhide6subtabs(div1,div2,div3,div4,div5,div6)
{
	var tab1 = document.getElementById(div1);
	var tab2 = document.getElementById(div2);
	var tab3 = document.getElementById(div3);
	var tab4 = document.getElementById(div4);
	var tab5 = document.getElementById(div5);
	var tab6 = document.getElementById(div6);
	
  	if (document.images)
  	{
		tab1.style.fontSize = "1em";
		tab1.style.fontWeight = "bolder";
		tab1.style.backgroundImage = "url(../../common/images/bg/subtab_bg.gif)"; 
		tab1.style.backgroundRepeat = "repeat-x"; 
		tab1.style.border= "1px solid #efeeec";
		tab1.style.backgroundPosition = "0 bottom"; 

		tab2.style.fontSize = "0.9em";
		tab2.style.fontWeight = "normal";
		tab2.style.backgroundImage = "none"; 
		tab2.style.border= "";

		tab3.style.fontSize = "0.9em";
		tab3.style.fontWeight = "normal";
		tab3.style.backgroundImage = "none"; 
		tab3.style.border= "";

		tab4.style.fontSize = "0.9em";
		tab4.style.fontWeight = "normal";
		tab4.style.backgroundImage = "none"; 
		tab4.style.border= "";

		tab5.style.fontSize = "0.9em";
		tab5.style.fontWeight = "normal";
		tab5.style.backgroundImage = "none"; 
		tab5.style.border= "";

		tab6.style.fontSize = "0.9em";
		tab6.style.fontWeight = "normal";
		tab6.style.backgroundImage = "none"; 
		tab6.style.border= "";
	}
}


function inputTest(input1, input2, input3)
{
	var element1 = document.getElementById(input1);
	var element2 = document.getElementById(input2);
	var element3 = document.getElementById(input3);

	if(element1.checked)
	{
		element2.disabled = false;
		element3.disabled = false;
	}
	else
	{
		element2.disabled = true;
		element3.disabled = true;
	}
}

function inputTest2(input1, input2, input3)
{
	var element1 = document.getElementById(input1);
	var element2 = document.getElementById(input2);
	var element3 = document.getElementById(input3);

	if(element1.checked)
	{
		element2.disabled = true;
		element3.disabled = true;
	}
	else
	{
		element2.disabled = false;
		element3.disabled = false;
	}
}

function subtabon(inputj)
{
	var j = document.getElementById(inputj);
	
	j.style.backgroundColor='#000000'; 
	j.style.color='#ffffff';
}

function subtaboff(inputk)
{
	var k = document.getElementById(inputk);
	
	k.style.backgroundColor=''; 
	k.style.color='';
}

function checkboxshowhide(checkboxname, actiondiv)
	{
	var element1 = document.getElementById(checkboxname);
	var element2 = document.getElementById(actiondiv);

	if(element1.checked)
		{
			element2.style.display = "block";
		}
		else
		{
			element2.style.display = "none";
		}   		
 	} 

function checkboxSelect()
	{
	if(document.getElementById("selectall").checked)
		{
		document.getElementById("checkbox1").checked = true;
		document.getElementById("checkbox2").checked = true;
		document.getElementById("checkbox3").checked = true;
		document.getElementById("checkbox4").checked = true;
		document.getElementById("checkbox5").checked = true;							
		}
	else
		{
		document.getElementById("checkbox1").checked = false;
		document.getElementById("checkbox2").checked = false;
		document.getElementById("checkbox3").checked = false;
		document.getElementById("checkbox4").checked = false;
		document.getElementById("checkbox5").checked = false;	
		}   		
	} 

function adddate(date, divID)
	{
		document.getElementById('startdate').value = date;					
		document.getElementById(divID).style.display = "none";
	} 
	
function moveMiddleBar(middleDiv,topDiv,div3,div4)
{
	var element1 = document.getElementById(middleDiv);
	var element2 = document.getElementById(topDiv);
	var element3 = document.getElementById(div3);
	var element4 = document.getElementById(div4);

  	if(element1.style.top != "0px")
  	{
		element1.style.top = "0px";
		element2.style.display = "none";
	}
	else
	{
		element1.style.top = "86px";
		element2.style.display = "block";
	} 

	if(element3.style.display == "none")
  	{
		element3.style.display = "block";
		element4.style.display = "none";		
	}
	else
	{
		element3.style.display = "none";
		element4.style.display = "block";
	} 
}

function showhideMenu(divID)
{
	var curMenu = document.getElementById(divID);
	
	if(curMenuID != null) //curMenuID is a global variable usually defined at the top of the local js file.
		{
			curMenuID.style.display = "none";
		}
	curMenuID = curMenu;
}