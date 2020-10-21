<?php

require 'conexionBBDD.php';

class operaciones
{

	private function comprobarValidezDatos($dato){
        if(empty(trim($dato)) or is_null($dato) or strpos($dato, ' ') !== FALSE){
            return false;
        }else{
            return true;
        }
    }
	
	
	private function existeUsuario($username){
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
				return "TRUE";
			}
		} catch (PDOException $e) {
			return "[Error]: no se pudo ejecutar la sentencia SQL.";
		}
	}
	
	
	private function existeCancion($tituloCancion){
		$consulta = "SELECT tituloCancion FROM canciones WHERE tituloCancion LIKE '{$tituloCancion}'";
		$comando = Database::getInstance()->getDb()->prepare($consulta);
		// Ejecutar sentencia preparada
		$comando->execute();
		
		$cadenaJSon = $comando->fetchAll(PDO::FETCH_ASSOC);
		if (empty($cadenaJSon)) 
		{
			return "FALSE";
		}
		else{
			return "TRUE";
		}
	}
	
	
	private function getIDUsuario($nombreUsuario){
		$consulta = "SELECT idUsuario FROM usuarios WHERE nombreUsuario LIKE '{$nombreUsuario}' || emailUsuario LIKE '{$nombreUsuario}'";
		$comando = Database::getInstance()->getDb()->prepare($consulta);
		// Ejecutar sentencia preparada
		$comando->execute();
		$cadenaJSon = $comando->fetchAll(PDO::FETCH_ASSOC);
		if (empty($cadenaJSon)) 
		{
			return "FALSE";
		}
		else{
			return $cadenaJSon[0]['idUsuario'];
		}
	}
	
	/*
	private function getIDCancion($tituloCancion){
		$consulta = "SELECT idCancion FROM canciones WHERE tituloCancion LIKE '{$tituloCancion}'";
		$comando = Database::getInstance()->getDb()->prepare($consulta);
		// Ejecutar sentencia preparada
		$comando->execute();
		$cadenaJSon = $comando->fetchAll(PDO::FETCH_ASSOC);
		if (empty($cadenaJSon)) 
		{
			return "FALSE";
		}
		else{
			return $cadenaJSon[0]['idCancion'];
		}
	}
	*/
	
	function insertarCancion($username, $password, $tituloCancion, $autorCancion, $duracion, $enlace)
    {
		if(operaciones::comprobarValidezDatos($username) == TRUE && operaciones::comprobarValidezDatos($password) == TRUE && operaciones::comprobarValidezDatos($tituloCancion) == TRUE && operaciones::comprobarValidezDatos($autorCancion) && operaciones::comprobarValidezDatos($duracion) && operaciones::comprobarValidezDatos($enlace)){
			$existeCancion = operaciones::existeCancion($tituloCancion);
			if($existeCancion == "FALSE"){
				$idUsuario = operaciones::getIDUsuario($username);
				if($idUsuario != "FALSE"){
					$consulta = "INSERT INTO canciones (tituloCancion, autorCancion, duracion, enlace, idUsuario) VALUES('{$tituloCancion}', '{$autorCancion}', '{$duracion}', '{$enlace}', '{$idUsuario}')";
					// Preparar sentencia
					$sentencia = Database::getInstance()->getDb()->prepare($consulta);
					$sentencia->execute();

					if($sentencia){
						$resultado = "TRUE";
					}else{
						$resultado = "[Error]: no se pudo realizar la insercción.";
					}
				}else{
					$resultado = "[Error]: no existe usuario.";
				}
			}else{
				$resultado = "[Error]: el titulo de la cancion ya existe.";
			}
		}else{
			$resultado = "[Error]: datos invalidos.";
		}
		$lista["datos"] = array(array("resultado" => $resultado));;
		echo json_encode($lista);
    }
	
	
	function getCanciones()
    {
		try {
			$consulta = "SELECT tituloCancion FROM canciones";
            // Preparar sentencia
            $comando = Database::getInstance()->getDb()->prepare($consulta);
            // Ejecutar sentencia preparada
            $comando->execute();
            $cadenaJSon=  $comando->fetchAll(PDO::FETCH_ASSOC);
        } catch (PDOException $e) {
            $cadenaJSon = "[Error]: no se pudo realizar la query.";
        }
		finally{
			$lista["datos"] = $cadenaJSon;
            echo json_encode($lista);
		}
	}
	
	
	function getCancionesUsuario($username, $password)
    {
		if(operaciones::comprobarValidezDatos($username) == TRUE && operaciones::comprobarValidezDatos($password) == TRUE){
			try {
				$consulta = "SELECT tituloCancion FROM canciones NATURAL JOIN usuarios WHERE (nombreUsuario LIKE '{$username}' OR emailUsuario LIKE '{$username}') AND password LIKE '{$password}'";
				// Preparar sentencia
				$comando = Database::getInstance()->getDb()->prepare($consulta);
				// Ejecutar sentencia preparada
				$comando->execute();
				$cadenaJSon=  $comando->fetchAll(PDO::FETCH_ASSOC);
			} catch (PDOException $e) {
				$cadenaJSon = "[Error]: no se pudo realizar la query.";
			}
			finally{
				$resultado = $cadenaJSon;
			}
		}
		else{
			$resultado = "[Error]: datos invalidos.";
		}
		$lista["datos"] = $resultado;
		echo json_encode($lista);
	}
	
	
	
	function getDatosCancion($tituloCancion){
		echo $tituloCancion;
		if(operaciones::comprobarValidezDatos($tituloCancion) == TRUE){
			$existeCancion = operaciones::existeCancion($tituloCancion);
			if($existeCancion == "TRUE"){
				try {
					$consulta = "SELECT tituloCancion, autorCancion, duracion, enlace, nombreUsuario FROM canciones NATURAL JOIN usuarios WHERE tituloCancion LIKE '{$tituloCancion}'";
					// Preparar sentencia
					$comando = Database::getInstance()->getDb()->prepare($consulta);
					// Ejecutar sentencia preparada
					$comando->execute();
					$cadenaJSon = $comando->fetchAll(PDO::FETCH_ASSOC);
				} catch (PDOException $e) {
					$cadenaJSon = "[Error]: no se pudo realizar la query.";
				}
				finally{
					$resultado = $cadenaJSon;
				}
			}else{
				$resultado = "[Error]: la cancion no existe.";
			}
		}else{
			$resultado = "[Error]: datos invalidos.";
		}
		$lista["datos"] = $resultado;
		echo json_encode($lista);
	}
	
	
	
	function actualizarCancion($username, $password, $tituloCancion, $tituloCancionOld, $autorCancion, $duracion, $enlace){
		if(operaciones::comprobarValidezDatos($username) == TRUE && operaciones::comprobarValidezDatos($password) == TRUE && operaciones::comprobarValidezDatos($tituloCancionOld) && operaciones::comprobarValidezDatos($tituloCancion) == TRUE && operaciones::comprobarValidezDatos($autorCancion) && operaciones::comprobarValidezDatos($duracion) && operaciones::comprobarValidezDatos($enlace)){
			$existeCancion = operaciones::existeCancion($tituloCancionOld);
			if($existeCancion == "TRUE"){
				try {
					$consulta = "UPDATE canciones SET tituloCancion = '{$tituloCancion}', autorCancion = '{$autorCancion}', duracion = {$duracion}, enlace = '{$enlace}' WHERE tituloCancion LIKE '{$tituloCancionOld}' AND idUsuario = (SELECT idUsuario FROM usuarios WHERE nombreUsuario LIKE '{$username}' OR emailUsuario LIKE '{$username}')";
					$sentencia = Database::getInstance()->getDb()->prepare($consulta);
					$sentencia->execute();
					if($sentencia){
						$resultado = "TRUE";

					}else{
						$resultado = "[Error]: no se pudo realizar la actualizacion.";
					}
				} catch (PDOException $e) {
					$resultado = "[Error]: no se pudo realizar la query.";
				}
			}else{
				$resultado = "[Error]: la cancion no existe.";
			}
		}
		else{
			$resultado = "[Error]: datos invalidos.";
		}
		$lista["datos"] = array(array("resultado" => $resultado));
		echo json_encode($lista);
	}
	
	
	function borrarCancion($username, $password, $tituloCancion){
		if(operaciones::comprobarValidezDatos($username) == TRUE && operaciones::comprobarValidezDatos($password) == TRUE && operaciones::comprobarValidezDatos($tituloCancion)){
			$existeCancion = operaciones::existeCancion($tituloCancion);
			if($existeCancion == "TRUE"){
				$consulta = "DELETE FROM canciones WHERE tituloCancion LIKE '{$tituloCancion}' AND idUsuario = (SELECT idUsuario FROM usuarios WHERE nombreUsuario LIKE '{$username}' OR emailUsuario LIKE '{$username}')";
				$sentencia = Database::getInstance()->getDb()->prepare($consulta);
				$sentencia->execute();
				if($sentencia){
					$resultado = "TRUE";

				}else{
					$resultado = "[Error]: no se pudo realizar el borrado de la cancion.";
				}
			}else{
				$resultado = "[Error]: la cancion no existe.";
			}
		}
		else{
			$resultado = "[Error]: datos invalidos.";
		}
		$lista["datos"] = array(array("resultado" => $resultado));
		echo json_encode($lista);
	}
	
	
	function modificarPassword($username, $passwordOld, $password){
		if(operaciones::comprobarValidezDatos($username) == TRUE && operaciones::comprobarValidezDatos($password) == TRUE && operaciones::comprobarValidezDatos($passwordOld)){
			if(operaciones::existeUsuario($username) == TRUE){
				$consulta = "UPDATE usuarios SET password = '{$password}' WHERE (idUsuario = nombreUsuario LIKE '{$username}' OR emailUsuario LIKE '{$username}') AND password LIKE '{$passwordOld}'";
				$sentencia = Database::getInstance()->getDb()->prepare($consulta);
				$sentencia->execute();
				if($sentencia){
					$resultado = "TRUE";
				}else{
					$resultado = "[Error]: no se pudo modificar la contraseña.";
				}
			}else{
				$resultado = "[Error]: el usuario no existe.";
			}
		}else{
			$resultado = "[Error]: datos invalidos.";
		}
		$lista["datos"] = array(array("resultado" => $resultado));
		echo json_encode($lista);
	}
	
	
	function borrarUsuario($username, $password){
		if(operaciones::comprobarValidezDatos($username) == TRUE && operaciones::comprobarValidezDatos($password) == TRUE){
			if(operaciones::existeUsuario($username) == TRUE){
				$consulta = "DELETE FROM canciones WHERE idUsuario = (SELECT idUsuario FROM usuarios WHERE (idUsuario = nombreUsuario LIKE '{$username}' OR emailUsuario LIKE '{$username}') AND password LIKE '{$password}')";
				$sentencia = Database::getInstance()->getDb()->prepare($consulta);
				$sentencia->execute();
				if($sentencia){
					$consulta = "DELETE FROM usuarios WHERE (idUsuario = nombreUsuario LIKE '{$username}' OR emailUsuario LIKE '{$username}') AND password LIKE '{$password}'";
					$sentencia = Database::getInstance()->getDb()->prepare($consulta);
					$sentencia->execute();
					if($sentencia){
						$resultado = "TRUE";
					}
					else{
						$resultado = "[Error]: no se pudo borrar el usuario.";
					}
				}else{
					$resultado = "[Error]: no se pudo borrar las canciones del usuario.";
				}
			}else{
				$resultado = "[Error]: el usuario no existe.";
			}
		}else{
			$resultado = "[Error]: datos invalidos.";
		}
		$lista["datos"] = array(array("resultado" => $resultado));
		echo json_encode($lista);
	}
}


if(isset($_GET["insertarCancion"])){
    $username = $_GET["nombreUsuario"];
    $password = $_GET["password"];
	$tituloCancion = $_GET["tituloCancion"];
	$autorCancion = $_GET["autorCancion"];
	$duracion = $_GET["duracion"];
	$enlace = $_GET["enlace"];
    operaciones::insertarCancion($username, $password, $tituloCancion, $autorCancion, $duracion, $enlace);
}

else if(isset($_GET["getCanciones"])){
    operaciones::getCanciones();
}

else if(isset($_GET["getCancionesUsuario"])){
    $username = $_GET["nombreUsuario"];
    $password = $_GET["password"];	
    operaciones::getCancionesUsuario($username, $password);
}

else if(isset($_GET["getDatosCancion"])){
	$tituloCancion = $_GET["tituloCancion"];
    operaciones::getDatosCancion($tituloCancion);
}

else if(isset($_GET["actualizarCancion"])){
	$username = $_GET["nombreUsuario"];
    $password = $_GET["password"];
	$tituloCancionOld = $_GET["tituloCancionOld"];
	$tituloCancion = $_GET["tituloCancion"];
	$autorCancion = $_GET["autorCancion"];
	$duracion = $_GET["duracion"];
	$enlace = $_GET["enlace"];
    operaciones::actualizarCancion($username, $password, $tituloCancion, $tituloCancionOld, $autorCancion, $duracion, $enlace);
}

else if(isset($_GET["borrarCancion"])){
	$username = $_GET["nombreUsuario"];
    $password = $_GET["password"];
	$tituloCancion = $_GET["tituloCancion"];
	operaciones::borrarCancion($username, $password, $tituloCancion);
}


else if(isset($_GET["modificarPassword"])){
	$username = $_GET["nombreUsuario"];
    $passwordOld = $_GET["passwordOld"];
	$password = $_GET["password"];
	operaciones::modificarPassword($username, $passwordOld, $password);
}

else if(isset($_GET["borrarUsuario"])){
	$username = $_GET["nombreUsuario"];
	$password = $_GET["password"];
	operaciones::borrarUsuario($username, $password);
}

?>