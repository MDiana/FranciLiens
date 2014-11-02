/**
 * 
 */

/*fonctions qui vérifient les données saisies avant submit */
function compareMail() {
	var para= document.getElementById('notif');
	
	// vérif des mails
	if (document.getElementById('email').value != document.getElementById('emailconf').value) {
		para.innerHTML = 'Le mail et sa confirmation ne sont pas les mêmes.';
		return false;
	} else {
		para.innerHTML = 'Les champs marqués d\'une * sont obligatoires. Huit caractères minimum pour le mot de passe.';
		return true;
	}
}
	
function veriflogin(){	
	//verif du login
	if( (document.getElementById('Login').value.length<1)
		|| (document.getElementById('Login').value.length >15)){
		
		document.getElementById('veriflogin').innerHTML= ' Incorrect';
		return false;
		
	} else {
		document.getElementById('veriflogin').innerHTML= '';
		return true;
	}
	
}

function verifpassword(){
	//verif du password
	if(document.getElementById('Password').value.length <8){
		
		document.getElementById('verifpassword').innerHTML = ' Invalide ';
		return false;
	} else {
		document.getElementById('verifpassword').innerHTML = '';
		return true;
	}
}

//verif du mail
function verifmail(){
	var regex = /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/;
	if((document.getElementById('email').value.length<1)
		|| (!regex.test(document.getElementById('email').value))){
		
		document.getElementById('verifmail1').innerHTML = ' Invalide';
		return false;
	} else {
		document.getElementById('verifmail1').innerHTML = '';
		return true;
	}
	
}	
	
//verif du mail
function verifmail2(){
	var regex = /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/;
	if((document.getElementById('emailconf').value.length<1) 
		|| (!regex.test(document.getElementById('emailconf').value))){
		
		document.getElementById('verifmail').innerHTML = ' Invalide';
		return false;
	} else {
		document.getElementById('verifmail').innerHTML = '';
		return true;
	}
	
}


function verifage(){
	//véifier les données de l'âge
	if((document.getElementById('ageu').value < 16)||(document.getElementById('ageu').value > 120)){
		document.getElementById('verifage').innerHTML = ' Incorrect';
		return false;
	}else{
		document.getElementById('verifage').innerHTML = '';
		return true;
	}
	
}

function compare(){
	//submit si tout est ok
	var comparemailOK= compareMail();
	var verifloginOk= veriflogin();
	var verifpasswordOK=verifpassword();
	var verifmailOk = verifmail2();
	var verifmail2Ok= verifmail();
	var verifageOk= verifage();
	
	if(comparemailOK && verifloginOk && verifpasswordOK && verifmailOk && verifmail2Ok && verifageOk)
	{
		document.getElementById('Register').submit();
	}
}



function checkAvatar() {
	var elem = document.getElementById('avatar');
	var url = elem.value;
	var image = new Image();
	image.src=url;
	if (image.height > 200 || image.width > 200) {
		document.getElementById('verifavatar').innerHTML = 'Trop grand !';
		return false;
	} else {
		document.getElementById('verifavatar').innerHTML = '';
		return true;
	}
}

function checkEditProfile() {
	var avatarOK = checkAvatar();
	if (avatarOK) {
		document.getElementById('editionprofilform').submit();
	}
}


function appelAjax(){
	var xhr= new XMLHttpRequest();
	// Rajouter le ?avec le paramètre de la gare :D
	xhr.open('get', 'http://franci-liens.appspot.com/prochainsDeparts');
	
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200){
		  
	  }
	} 
}

function affichageProchainsDepart(arr){
	// récupérer l'encart et ajouter un element de la liste pour tous les objets récupérés
	// pour récupérer un objet on peut faire obj.lenom del'attribut qu'on veut (voir dans le servlet des prochains départs)
	var tableTrajets = document.getElementById('trajetsEnregistres');
	var ligne = "";
	//var num = "";
	var idPassage ;
	var date ;
	var mission = "";
	var term = "";
	var objDepart ;
	var train = new Array();
	for (i=0; i< arr.length; i++ ){
		var objDepart = arr[i];
		//num = objDepart.num;
		idPassage = objDepart.idPassage;
		date = objDepart.date;
		mission = objDepart.mission;
		term = objDepart.term;
		//construction dynamique d'une ligne de tableau
	//<a href="/enregistrementTrajet?num=PACA42"><img src="images/check32.png"></a>	
		var infos = new Array();
		infos.push(date); 
		infos.push(mission);
		infos.push(term);
		var tr = document.createElement('tr');
		for(j = 0; j<4; j++){
			var td = document.createElement('td');
			if(j==3){
				var url = "/enregistrementTrajet?idPassage="+idPassage;
				var adr = document.createElement('a');
				adr.setAttribute('href', url);
				
				var imgSrc = "images/check32.png";
				var img = document.createElement('img');
				img.setAttribute('src', imgSrc);
				
				td.appendChild(adr);
				td.appendChild(img);
			}
			else{
				td.appendChild(document.createTextNode(infos[j]));
			}
			tr.appendChild(td);
			
		}
		tableTrajets.appendChild(tr);
	}
}

function majTrains() {
	
	var gare = document.getElementById('gare').value;
	
	//var url = "http://franci-liens.appspot.com/prochainsDeparts?"+gare;
	var url = "localhost:8888/prochainsDeparts?gare="+gare;
	var xhr = new XMLHttpRequest();
	alert ("xmlHttpRequest=" + xmlHttpRequest); 
	xhr.open('get', url, true);
	
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4 && xhr.status == 200){
			alert("appel ajax fonctionne 1");
			var departsArr =  JSON.parse(xhr.responseText);
			affichageProchainsDepart(departsArr);
		}
	}
	xhr.send(null);
}


/*
 * Màj dynamique de la liste des trains enregistrés par les
 * autres usagers lorsque l'utilisateur change la gare de départ
 * via le select
 */

function majTrainsEnregistres(){
	var garenom= document.getElementById('gareSelect').value;
	var url= "http://localhost:8888/trajetsenregistresaff?gare="+gare;
	var xhr = new XMLHttpRequest();
	xhr.open('get', url, true);
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4 && xhr.status == 200){
			alert("appel ajax fonctionne 1");
			// a-t-on vraiment besoin de parser? Ne récupère-t-on pas directement un objet JSON et pas une chaîne de caractère?
			var trajets =  JSON.parse(xhr.responseText);
			//var trajets = eval('(' + xhr.responseText + ')');
			affichageTrajetsEnregistres(trajets);
		}
	}
	xhr.send(null);
}

function affichageTrajetsEnregistres(tabTrajets){
	alert('entree dans la fonction');
	var tbody= document.getElementById('trajets').getElementsByTagName('tbody')[0];
	var tr, td;
	for(var i=0; i<tabTrajets.length; i++){

		alert('on a un élément');
		//création de la ligne
		tr = document.createElement('tr');
		
		//puis des colonnes
		var photo = tabTrajets[i].avatarmini;
		td = document.createElement('td');
		td.appendChild(photo);
		tr.appendChild(td);
		var pseudo = tabTrajets[i].pseudo;
		td = document.createElement('td');
		td.appendChild(pseudo);
		tr.appendChild(td);
		var age = tabTrajets[i].age;
		td = document.createElement('td');
		td.appendChild(age);
		tr.appendChild(td);
		var term = tabTrajets[i].term;
		td = document.createElement('td');
		td.appendChild(term);
		tr.appendChild(td);
		var description = tabTrajets[i].description;
		td = document.createElement('td');
		td.appendChild(description);
		tr.appendChild(td);
		td = document.createElement('td');
		var a= document.createElement('a');
		a.setAttribute('href', 'http://google.com');
		var img= document.createElement('img');
		img.setAttribute('src', 'images/invite32.png');
		a.appendChild(img);
		td.appendChild(a);
		alert("age" + age);
		
		tbody.appendChild(tr);
		//tableauOuAfficher.appendChild(tbody);
	}
	
}
