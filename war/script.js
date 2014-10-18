/**
 * 
 */

/*fonction qui vérifie les données saisies avant submit */
function compareMail() {
	var regex = /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/;
	var verif= 5;
	var para= document.getElementById('notif');
	
	// vérif des mails
	if (document.getElementById('email').value != document.getElementById('emailconf').value) {
		para.innerHTML = 'Le mail et sa confirmation ne sont pas les mêmes.';
	
	}else{
		para.innerHTML = 'Les champs marqués d\'une * sont obligatoires. Mot de passe: 8 caractères min.';
		verif--;
	}
	
	
	//verif du login
	if( (document.getElementById('Login').value.length<1)
		|| (document.getElementById('Login').value.length >15)){
		
		document.getElementById('veriflogin').innerHTML= ' Login invalide';
		
	}else{
		document.getElementById('veriflogin').innerHTML= '';
		verif--;
	}
	
	//verif du password
	if(document.getElementById('Password').value.length <8){
		
		document.getElementById('verifpassword').innerHTML = ' Mot de passe invalide ';
		
	}else{
		document.getElementById('verifpassword').innerHTML = '';
		verif--;
	}
	
	
	//verif du mail
	if((document.getElementById('email').value.length<1)
		|| (!regex.test(document.getElementById('email').value))){
		
		document.getElementById('verifmail1').innerHTML = ' Mail invalide.';
		
	}else{
		document.getElementById('verifmail1').innerHTML = '';
		verif--;
	}
	
	
	//verif du mail
	if((document.getElementById('emailconf').value.length<1) 
		|| (!regex.test(document.getElementById('emailconf').value))){
		
		document.getElementById('verifmail').innerHTML = ' Mail invalide';		
	}else{
		document.getElementById('verifmail').innerHTML = '';
		verif--;
	}
	
	
	//submit si tout est ok
	if(verif==0)
	{
	document.getElementById('Register').submit();
	}
}
