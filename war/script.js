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

/*
 * Màj dynamique de la liste des trains enregistrés par les
 * autres usagers lorsque l'utilisateur change la gare de départ
 * via le select
 */
function majTrainsEnregistres() {
	
	var codeGare = document.getElementById('gareSelect').value;
	
	
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


function affichageProchainsDepart(arr){
	// récupérer l'encart et ajouter un element de la liste pour tous les objets récupérés
	// pour récupérer un objet on peut faire obj.lenom del'attribut qu'on veut (voir dans le servlet des prochains départs)
	var tableTrajets = document.getElementById('trajets');
	
//	var tbody = tableTrajets.getElementsByTagName('tbody');
//	var listDesTr = tbody.children;
//	for(var i=1; i< listDesTr.length; i++){
//		while (listDesTr[i].firstChild) {
//			listDesTr[i].removeChild(listDesTr[i].firstChild);
//			}
//	}
	var listDesTr = tableTrajets.getElementsByTagName('tr');
	for(var i=1; i< listDesTr.length; i++){
		while (listDesTr[i].firstChild) {
			listDesTr[i].removeChild(listDesTr[i].firstChild);
			}
	}
	
	var ligne = "";
	//var num = "";
	var idPassage ;
	var date ;
	var mission = "";
	var term = "";
	var objDepart ;
	var train = new Array();
	var res = arr.prochainsDeparts;
	var count = res.length;
	for (i=0; i< count; i++ ){
		var objDepart = res[i];
		//num = objDepart.num;
		idPassage = objDepart.idPassage;
		date = objDepart.date;
		mission = objDepart.mission;
		term = objDepart.term;
		//construction dynamique d'une ligne de tableau	
		var infos = new Array();
		infos.push(date); 
		infos.push(mission);
		infos.push(term);
		var tr = document.createElement('tr');
		for(j = 0; j<4; j++){
			var td = document.createElement('td');
			if(j==3){
				var url = "http://franci-liens.appspot.com/enregistrertrajet?idPassage="+idPassage;
				var adr = document.createElement('a');
				adr.setAttribute('href', url);
				
				var imgSrc = "http://franci-liens.appspot.com/images/check32.png";
				var img = document.createElement('img');
				img.setAttribute('src', imgSrc);
				adr.appendChild(img);
				td.appendChild(adr);
			}
			else{
				td.appendChild(document.createTextNode(infos[j]));
			}
			tr.appendChild(td);
			
		}
		tableTrajets.appendChild(tr);
	}
}

/*
 * mise à jour des prochains départs
 */
function majTrains() {
	var gare = document.getElementById('gareSelect').value;

	var url = "http://franci-liens.appspot.com/prochainsdeparts?gare="+gare;
	var xhr = new XMLHttpRequest();

	xhr.open('get', url);
	
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4 && xhr.status == 200){
			var departsArr =  JSON.parse(xhr.responseText);
			affichageProchainsDepart(departsArr);
		}
	}
	xhr.send(null);
}
/*
 * mise à jour des trajets enregistrés par les utilisateurs suivant 
 * le gare sélectionnée par l'utilisateur
 */

function majTrainsEnregistres(){
	var gare= document.getElementById('gareSelect').value;
	var url= "http://franci-liens.appspot.com/trajetsenregistresaff?gare="+gare;
	var xhr = new XMLHttpRequest(); 
	xhr.open('get', url, true);
	
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4 && xhr.status == 200){
			var data =  JSON.parse(xhr.responseText);
			affichageTrajetsEnregistres(data);
		}
	}
	xhr.send(null);
}


function affichageTrajetsEnregistres(data){
	var tbody= document.getElementById('trajets').getElementsByTagName('tbody')[0];
	
	//suppression de tous les éléments à chaque tour
	var listDesTr = tbody.getElementsByTagName('tr');
	for(var i=1; i< listDesTr.length; i++){
		while (listDesTr[i].firstChild) {
			listDesTr[i].removeChild(listDesTr[i].firstChild);
			}
	}
	
	var tr, td;
	var res= data.trajetsEnregistres;
	var count= res.length;
	if(count>0){
	for(var i=0; i<count; i++){

		//création de la ligne
		tr = document.createElement('tr');
		
		//puis des colonnes
		var photo = res[i].avatarmini;
		td = document.createElement('td');
		var miniavatar= document.createElement('img');
		miniavatar.setAttribute('class', 'miniavatar');
		miniavatar.setAttribute('src', photo);
		td.appendChild(miniavatar);
		tr.appendChild(td);
		
		var pseudo = res[i].pseudo;
		td = document.createElement('td');
		var text= document.createTextNode(pseudo);
		td.appendChild(text);
		tr.appendChild(td);
		var age = res[i].age;
		td = document.createElement('td');
		var text= document.createTextNode(age);
		td.appendChild(text);
		tr.appendChild(td);
		var term = res[i].term;
		td = document.createElement('td');
		var text= document.createTextNode(term);
		td.appendChild(text);
		tr.appendChild(td);
		var description = res[i].description;
		td = document.createElement('td');
		td.setAttribute('class', description);
		var text= document.createTextNode(description);
		td.appendChild(text);
		tr.appendChild(td);
		
		td = document.createElement('td');
		var a= document.createElement('a');
		var image= document.createElement('img');
		a.setAttribute('href', 'http://franci-liens.appspot.com/invite?recipient=' + pseudo);
		image.setAttribute('src', 'http://franci-liens.appspot.com/images/invite32.png');
		a.appendChild(image);
		td.appendChild(a);
		tr.appendChild(td);
		
		
		tbody.appendChild(tr);
	}
	
	}else{
		// remettre le tableau à vide...
		var listDesTr = tbody.getElementsByTagName('tr');
		for(var i=1; i< listDesTr.length; i++){
			while (listDesTr[i].firstChild) {
				listDesTr[i].removeChild(listDesTr[i].firstChild);
				}
		}
		
	}
	
}

