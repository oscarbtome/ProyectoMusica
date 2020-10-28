<?php

require 'conexionBBDD.php';


class consultas
{

	private function comprobarValidezDatos($dato){
        if(empty(trim($dato)) or is_null($dato) or strpos($dato, ' ') !== FALSE){
            return false;
        }else{
            return true;
        }
    }
	
	
	private function getRol($username){
		try {
			$consulta = "SELECT rol FROM roles NATURAL JOIN usuarios WHERE nombreUsuario LIKE '{$username}'";
			// Preparar sentencia
			$comando = Database::getInstance()->getDb()->prepare($consulta);
			// Ejecutar sentencia preparada
			$comando->execute();

			$cadenaJSon=  $comando->fetchAll(PDO::FETCH_ASSOC);
			if (empty($cadenaJSon)) 
			{
				return "[Error]: rol invalido.";
			}
			else{
				if($cadenaJSon[0]['rol'] == 'user' || $cadenaJSon[0]['rol'] == 'admin' || $cadenaJSon[0]['rol'] == 'superAdmin'){
					return "TRUE";
				}
				return "[Error]: rol invalido.";
			}
		} catch (PDOException $e) {
			return "[Error]: no se pudo ejecutar la sentencia SQL.";
		}
	}
	
	
	private function existeUsuario($username){ //Comprobar solo nombre de usuario 
		try {
			$consulta = "SELECT nombreUsuario FROM usuarios WHERE nombreUsuario LIKE '{$username}' OR emailUsuario LIKE '{$username}'";
			// Preparar sentencia
			$comando = Database::getInstance()->getDb()->prepare($consulta);
			// Ejecutar sentencia preparada
			$comando->execute();
			$cadenaJSon=  $comando->fetchAll(PDO::FETCH_ASSOC);
			if (empty($cadenaJSon)) 
			{
				return "FALSE";
			}
			else{
				return "[Error]: este usuario ya esta registrado.";
			}
		} catch (PDOException $e) {
			return "[Error]: no se pudo ejecutar la sentencia SQL. ESTA 3";
		}
	}


   
    function comprobarLogin($username, $password) { //Comprobar login completo
		if(consultas::comprobarValidezDatos($username) == TRUE && consultas::comprobarValidezDatos($password) == TRUE){
			try {
				$consulta = "SELECT nombreUsuario FROM usuarios WHERE (nombreUsuario LIKE '{$username}' OR emailUsuario LIKE '{$username}') AND password LIKE '{$password}'";
				// Preparar sentencia
				$comando = Database::getInstance()->getDb()->prepare($consulta);
				// Ejecutar sentencia preparada
				$comando->execute();

				$cadenaJSon=  $comando->fetchAll(PDO::FETCH_ASSOC);
				if (empty($cadenaJSon)) 
				{
					$resultado = "[Error]: usuario o contraseña incorrectas.";
				}
				else{
					$resultado = consultas::getRol($username); //Comprobar rol de usuario y devolver resultado
				}
			} catch (PDOException $e) {
				$resultado = "[Error]: no se pudo ejecutar la sentencia SQL.";
			}
		}else{
			$resultado = "[Error]: datos invalidos.";
		}
		$lista["datos"] = array(array("resultado" => $resultado));;
		echo json_encode($lista);
    }
	


    function registrarUsuario($username, $password, $email)
    {
		if(consultas::comprobarValidezDatos($username) == true && consultas::comprobarValidezDatos($password) == true && consultas::comprobarValidezDatos($email) == true){
			$existeUsuario = consultas::existeUsuario($username);
			if($existeUsuario == "FALSE"){
				$consulta = "INSERT INTO usuarios (nombreUsuario, password, emailUsuario) VALUES('{$username}', '{$password}', '{$email}')";
				// Preparar sentencia
				$sentencia = Database::getInstance()->getDb()->prepare($consulta);
				$sentencia->execute();

				if($sentencia){
					$consulta2 = "INSERT INTO roles (idUsuario, rol) VALUES ((SELECT idUsuario FROM usuarios WHERE nombreUsuario LIKE '{$username}'), 'user')";
					// Preparar sentencia
					$sentencia2 = Database::getInstance()->getDb()->prepare($consulta2);
					$sentencia2->execute();
					if($sentencia2){
						$resultado = "TRUE";
					}
					else{
						$resultado = "[Error]: no se pudo realizar la insercción.";
					}
				}else{
					$resultado = "[Error]: no se pudo realizar la insercción.";
				}
			}else{
				$resultado = $existeUsuario;
			}
		}else{
			$resultado = "[Error]: datos invalidos.";
		}
		$lista["datos"] = array(array("resultado" => $resultado));;
		echo json_encode($lista);
    }
	
}


if(isset($_GET["comprobarLogin"])){
    $username = $_GET["nombreUsuario"];
    $password = $_GET["password"];
    consultas::comprobarLogin($username, $password);
}


else if(isset($_GET["registrarUsuario"])){
    $username = $_GET["nombreUsuario"];
    $password = $_GET["password"];
	$email = $_GET["emailUsuario"];
    consultas::registrarUsuario($username, $password, $email);
}

else{
	$resultado = "[Error]: operacion no permitida.";
	$lista["datos"] = array(array("resultado" => $resultado));
	echo json_encode($lista);
}

?>



