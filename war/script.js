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
		para.innerHTML = 'Les champs marqués d\'une * sont obligatoires. Mot de passe: 8 caractères min.';
		return true;
	}
}
	
function veriflogin(){	
	//verif du login
	if( (document.getElementById('Login').value.length<1)
		|| (document.getElementById('Login').value.length >15)){
		
		document.getElementById('veriflogin').innerHTML= ' Incorrect';
		return false
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
		
		document.getElementById('verifmail1').innerHTML = ' Invalide.';
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

function compare(){
	//submit si tout est ok
	var comparemailOK= compareMail();
	var verifloginOk= veriflogin();
	var verifpasswordOK=verifpassword();
	var verifmailOk = verifmail2();
	var verifmail2Ok= verifmail();
	
	if(comparemailOK && verifloginOk && verifpasswordOK && verifmailOk && verifmail2Ok)
	{
	document.getElementById('Register').submit();
	}
}
	

