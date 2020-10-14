<?php

/**
 * Representa el la estructura de las metas
 * almacenadas en la base de datos
 */
require 'conexionBBDD.php';



class consultas
{

	private function comprobarValidezDatos($dato){
        if(empty(trim($dato)) or is_null($dato) or strpos($dato, ' ') !== FALSE){
            return false;
        }
        else{
            return true;
        }
    }


    /**
     * Funcion para comprobar que existe el usuario en la BD, y con que atributo esta registrado (cliente o admin)
     *
     * @return array devuelve registro con nombre del usuario y el tipo del usuario para pasarlo al panel de cliente
     * o del administrador
     */
    function comprobarLogin($username, $password)
    {
        $consulta = "SELECT nombreUsuario, emailUsuario FROM usuarios WHERE (nombreUsuario LIKE '{$username}' OR emailUsuario LIKE '{$username}') AND password LIKE '{$password}'";

        try {
            // Preparar sentencia
            $comando = Database::getInstance()->getDb()->prepare($consulta);
            // Ejecutar sentencia preparada
            $comando->execute();

            $cadenaJSon=  $comando->fetchAll(PDO::FETCH_ASSOC);
            $lista["datosLogin"] = $cadenaJSon;

            echo json_encode($lista);

        } catch (PDOException $e) {
            return false;
        }
    }
	
	
	function existeUsuario($username)
    {
        $consulta = "SELECT nombreUsuario, emailUsuario FROM usuarios WHERE nombreUsuario LIKE '{$username}' || emailUsuario LIKE '{$username}'";

        try {
            // Preparar sentencia
            $comando = Database::getInstance()->getDb()->prepare($consulta);
            // Ejecutar sentencia preparada
            $comando->execute();

            $cadenaJSon=  $comando->fetchAll(PDO::FETCH_ASSOC);
            $lista["datosLogin"] = $cadenaJSon;

            echo json_encode($lista);

        } catch (PDOException $e) {

            return false;
        }
    }




     function insertarUsuario($username, $password, $email)
    {
        //Comprobacion de si tipo de usuario = Admin or Cliente y si el nombre, la password y el tipo de usuario no es nulo y no contiene espacios
        //if(consultas::comprobarValidezDatos($username) and consultas::comprobarValidezDatos($password) and consultas::comprobarValidezDatos($email)){
            $consulta = "INSERT INTO usuarios (nombreUsuario, password, emailUsuario) VALUES('{$username}', '{$password}', '{$email}')";

            // Preparar sentencia
            $sentencia = Database::getInstance()->getDb()->prepare($consulta);

            $sentencia->execute();

            if($sentencia){
                $respuesta["datosLogin"] = "OK";
                echo json_encode($respuesta);

            }else{
                $respuesta["datosLogin"] = "ERROR:2";
                echo json_encode($respuesta);

            }
        //}
		/*
        else{
            $respuesta["datosLogin"] = "ERROR:1";
            echo json_encode($respuesta);
        }*/
    }
}




if(isset($_GET["comprobarLogin"])){
    $username = $_GET["nombreUsuario"];
    $password = $_GET["password"];
    consultas::comprobarLogin($username, $password);
}


else if(isset($_GET["existeUsuario"])){
    $username = $_GET["nombreUsuario"];
    consultas::existeUsuario($username);
}


else if(isset($_GET["insertUsuario"])){
    $username = $_GET["nombreUsuario"];
    $password = $_GET["password"];
	$email = $_GET["emailUsuario"];
    consultas::insertarUsuario($username, $password, $email);
}



?>



