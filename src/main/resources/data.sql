-- Datos de prueba: 5 filas por tabla
-- Cada bloque inserta datos solo si la tabla está vacía (evita duplicados en cada arrancada)

-- Administradores
INSERT INTO administradores( nombre, apellido, correo, contrasena, telefono)
SELECT t.nombre, t.apellido, t.correo, t.contrasena, t.telefono
FROM (
  SELECT 'Admin' AS nombre, 'Uno' AS apellido, 'admin1@taller.com' AS correo, 'pass1' AS contrasena, '555-0001' AS telefono
  UNION ALL SELECT 'Admin','Dos','admin2@taller.com','pass2','555-0002'
  UNION ALL SELECT 'Admin','Tres','admin3@taller.com','pass3','555-0003'
  UNION ALL SELECT 'Admin','Cuatro','admin4@taller.com','pass4','555-0004'
  UNION ALL SELECT 'Admin','Cinco','admin5@taller.com','pass5','555-0005'
) AS t
WHERE NOT EXISTS (SELECT 1 FROM administradores LIMIT 1);

-- Clientes
INSERT INTO clientes( nombre, apellido, correo, contrasena)
SELECT t.nombre, t.apellido, t.correo, t.contrasena
FROM (
  SELECT 'Cliente' AS nombre, 'Uno' AS apellido, 'cliente1@taller.com' AS correo, 'cpass1' AS contrasena
  UNION ALL SELECT 'Cliente','Dos','cliente2@taller.com','cpass2'
  UNION ALL SELECT 'Cliente','Tres','cliente3@taller.com','cpass3'
  UNION ALL SELECT 'Cliente','Cuatro','cliente4@taller.com','cpass4'
  UNION ALL SELECT 'Cliente','Cinco','cliente5@taller.com','cpass5'
) AS t
WHERE NOT EXISTS (SELECT 1 FROM clientes LIMIT 1);

-- Mecanicos
INSERT INTO mecanicos( nombre, apellido, telefono)
SELECT t.nombre, t.apellido, t.telefono
FROM (
  SELECT 'Mec' AS nombre, 'Uno' AS apellido, '600-0001' AS telefono
  UNION ALL SELECT 'Mec','Dos','600-0002'
  UNION ALL SELECT 'Mec','Tres','600-0003'
  UNION ALL SELECT 'Mec','Cuatro','600-0004'
  UNION ALL SELECT 'Mec','Cinco','600-0005'
) AS t
WHERE NOT EXISTS (SELECT 1 FROM mecanicos LIMIT 1);

-- Proveedores
INSERT INTO proveedores( nombre_empresa, contacto, telefono, correo)
SELECT t.nombre_empresa, t.contacto, t.telefono, t.correo
FROM (
  SELECT 'Proveedor A' AS nombre_empresa, 'Contacto A' AS contacto, '700-0001' AS telefono, 'provA@proveedor.com' AS correo
  UNION ALL SELECT 'Proveedor B','Contacto B','700-0002','provB@proveedor.com'
  UNION ALL SELECT 'Proveedor C','Contacto C','700-0003','provC@proveedor.com'
  UNION ALL SELECT 'Proveedor D','Contacto D','700-0004','provD@proveedor.com'
  UNION ALL SELECT 'Proveedor E','Contacto E','700-0005','provE@proveedor.com'
) AS t
WHERE NOT EXISTS (SELECT 1 FROM proveedores LIMIT 1);

-- Categorias de Producto
INSERT INTO categorias_producto(nombre)
SELECT t.nombre FROM (
  SELECT 'Aceites' AS nombre
  UNION ALL SELECT 'Filtros'
  UNION ALL SELECT 'Frenos'
  UNION ALL SELECT 'Iluminacion'
  UNION ALL SELECT 'Neumaticos'
) AS t
WHERE NOT EXISTS (SELECT 1 FROM categorias_producto LIMIT 1);

-- Marcas Producto
INSERT INTO marcas_producto( nombre)
SELECT t.nombre FROM (
  SELECT 'MarcaA' AS nombre
  UNION ALL SELECT 'MarcaB'
  UNION ALL SELECT 'MarcaC'
  UNION ALL SELECT 'MarcaD'
  UNION ALL SELECT 'MarcaE'
) AS t
WHERE NOT EXISTS (SELECT 1 FROM marcas_producto LIMIT 1);

-- Productos Inventario
INSERT INTO productos_inventario( id_proveedor, nombre, descripcion, id_categoria, especificacion, precio_unitario, stock_actual, stock_minimo, id_marca, fecha_entrada)
SELECT t.id_proveedor, t.nombre, t.descripcion, t.id_categoria, t.especificacion, t.precio_unitario, t.stock_actual, t.stock_minimo, t.id_marca, t.fecha_entrada
FROM (
  SELECT 1 AS id_proveedor, 'Aceite 5W-30' AS nombre, 'Aceite sintético 1L' AS descripcion, 1 AS id_categoria, 'API SP' AS especificacion, 25.50 AS precio_unitario, 100 AS stock_actual, 10 AS stock_minimo, 1 AS id_marca, '2025-01-02' AS fecha_entrada
  UNION ALL SELECT 2,'Filtro de aceite','Filtro para motor',2,'STD',8.75,50,5,2,'2025-02-10'
  UNION ALL SELECT 3,'Pastillas de freno','Juego delantero',3,'CERAMIC',45.00,30,3,3,'2025-03-05'
  UNION ALL SELECT 4,'Bombilla H4','Bombilla halógena',4,'12V',12.00,20,2,4,'2025-01-20'
  UNION ALL SELECT 5,'Neumático 205/55','Neumático radial',5,'RIM17',85.00,15,2,5,'2025-02-28'
) AS t
WHERE NOT EXISTS (SELECT 1 FROM productos_inventario LIMIT 1);

-- Vehículos
INSERT INTO vehiculos( id_cliente, placas, color, modelo, marca, ano)
SELECT t.id_cliente, t.placas, t.color, t.modelo, t.marca, t.ano
FROM (
  SELECT 1 AS id_cliente, 'ABC-123' AS placas, 'Rojo' AS color, 'Model X' AS modelo, 'TOYOTA' AS marca, 2018 AS ano
  UNION ALL SELECT 2,'DEF-456','Azul','Model Y','TOYOTA',2020
  UNION ALL SELECT 3,'GHI-789','Blanco','Model Z','TOYOTA',2015
  UNION ALL SELECT 4,'JKL-012','Negro','Model A','TOYOTA',2022
  UNION ALL SELECT 5,'MNO-345','Gris','Model B','TOYOTA',2010
) AS t
WHERE NOT EXISTS (SELECT 1 FROM vehiculos LIMIT 1);

-- Citas
INSERT INTO citas( fecha_cita, id_empleado, id_cliente, tipo_cita, id_vehiculo, estado_cita, comentario)
SELECT t.fecha_cita, t.id_empleado, t.id_cliente, t.tipo_cita, t.id_vehiculo, t.estado_cita, t.comentario
FROM (
  SELECT '2025-06-10' AS fecha_cita, 1 AS id_empleado, 1 AS id_cliente, 'mantenimiento' AS tipo_cita, 1 AS id_vehiculo, 'pendiente' AS estado_cita, 'Revisión general' AS comentario
  UNION ALL SELECT '2025-06-11',2,2,'reparación',2,'en proceso','Fallo en transmisión'
  UNION ALL SELECT '2025-06-12',3,3,'diagnóstico',3,'finalizada','Sensor cambiado'
  UNION ALL SELECT '2025-06-13',4,4,'mantenimiento',4,'cancelada','Cliente canceló'
  UNION ALL SELECT '2025-06-14',5,5,'reparación',5,'pendiente','Alineación y balanceo'
) AS t
WHERE NOT EXISTS (SELECT 1 FROM citas LIMIT 1);

-- Trabajos Realizados
INSERT INTO trabajos_realizados( id_cita, descripcion, mano_obra, total_trabajo)
SELECT t.id_cita, t.descripcion, t.mano_obra, t.total_trabajo
FROM (
  SELECT 1 AS id_cita, 'Cambio de aceite' AS descripcion, 30.00 AS mano_obra, 55.50 AS total_trabajo
  UNION ALL SELECT 2,'Reparación caja',150.00,500.00
  UNION ALL SELECT 3,'Diagnóstico eléctrico',40.00,70.00
  UNION ALL SELECT 4,'Revisión general',25.00,25.00
  UNION ALL SELECT 5,'Alineación',20.00,120.00
) AS t
WHERE NOT EXISTS (SELECT 1 FROM trabajos_realizados LIMIT 1);

-- Detalle Uso Producto
INSERT INTO detalle_uso_producto( id_trabajo_realizado, id_producto_inventario, cantidad_usada, precio_unitario, subtotal)
SELECT t.id_trabajo_realizado, t.id_producto_inventario, t.cantidad_usada, t.precio_unitario, t.subtotal
FROM (
  SELECT 1 AS id_trabajo_realizado,1 AS id_producto_inventario,1 AS cantidad_usada,25.50 AS precio_unitario,25.50 AS subtotal
  UNION ALL SELECT 2,3,2,45.00,90.00
  UNION ALL SELECT 3,2,1,8.75,8.75
  UNION ALL SELECT 4,4,1,12.00,12.00
  UNION ALL SELECT 5,5,4,85.00,340.00
) AS t
WHERE NOT EXISTS (SELECT 1 FROM detalle_uso_producto LIMIT 1);

-- Reportes
INSERT INTO reportes( id_cita, descripcion_general, total)
SELECT t.id_cita, t.descripcion_general, t.total
FROM (
  SELECT 1 AS id_cita, 'Reporte cita 1' AS descripcion_general, 55.50 AS total
  UNION ALL SELECT 2,'Reporte cita 2',590.00
  UNION ALL SELECT 3,'Reporte cita 3',78.75
  UNION ALL SELECT 4,'Reporte cita 4',37.00
  UNION ALL SELECT 5,'Reporte cita 5',460.00
) AS t
WHERE NOT EXISTS (SELECT 1 FROM reportes LIMIT 1);

-- Datos recientes para pruebas de ingresos (fecha actual aproximada 2025-10)
INSERT INTO citas( fecha_cita, id_empleado, id_cliente, tipo_cita, id_vehiculo, estado_cita, comentario)
SELECT t.fecha_cita, t.id_empleado, t.id_cliente, t.tipo_cita, t.id_vehiculo, t.estado_cita, t.comentario
FROM (
  SELECT '2025-10-14' AS fecha_cita, 1 AS id_empleado, 1 AS id_cliente, 'mantenimiento' AS tipo_cita, 1 AS id_vehiculo, 'pendiente' AS estado_cita, 'Cita semana' AS comentario
  UNION ALL SELECT '2025-10-16',2,2,'reparación',2,'finalizada','Cita hoy'
  UNION ALL SELECT '2025-10-10',3,3,'diagnóstico',3,'finalizada','Cita inicio mes'
) AS t
WHERE NOT EXISTS (SELECT 1 FROM citas c WHERE c.fecha_cita = t.fecha_cita AND c.id_cliente = t.id_cliente LIMIT 1);

-- Insertar reportes asociados a las citas recientes (usa el id de la cita recién creada)
INSERT INTO reportes( id_cita, descripcion_general, total)
SELECT c.id_cita, 'Reporte prueba semana', 150.00
FROM citas c
WHERE c.fecha_cita = '2025-10-14' AND c.id_cliente = 1
  AND NOT EXISTS (SELECT 1 FROM reportes r WHERE r.id_cita = c.id_cita LIMIT 1);

INSERT INTO reportes( id_cita, descripcion_general, total)
SELECT c.id_cita, 'Reporte prueba hoy', 320.00
FROM citas c
WHERE c.fecha_cita = '2025-10-16' AND c.id_cliente = 2
  AND NOT EXISTS (SELECT 1 FROM reportes r WHERE r.id_cita = c.id_cita LIMIT 1);

INSERT INTO reportes( id_cita, descripcion_general, total)
SELECT c.id_cita, 'Reporte prueba mes', 80.00
FROM citas c
WHERE c.fecha_cita = '2025-10-10' AND c.id_cliente = 3
  AND NOT EXISTS (SELECT 1 FROM reportes r WHERE r.id_cita = c.id_cita LIMIT 1);

-- Opcional: agregar trabajos_realizados vinculados a las nuevas citas para mantener consistencia
INSERT INTO trabajos_realizados( id_cita, descripcion, mano_obra, total_trabajo)
SELECT c.id_cita, 'Trabajo demo', 40.00, 190.00
FROM citas c
WHERE c.fecha_cita = '2025-10-14' AND c.id_cliente = 1
  AND NOT EXISTS (SELECT 1 FROM trabajos_realizados t WHERE t.id_cita = c.id_cita LIMIT 1);

INSERT INTO trabajos_realizados( id_cita, descripcion, mano_obra, total_trabajo)
SELECT c.id_cita, 'Trabajo demo 2', 70.00, 390.00
FROM citas c
WHERE c.fecha_cita = '2025-10-16' AND c.id_cliente = 2
  AND NOT EXISTS (SELECT 1 FROM trabajos_realizados t WHERE t.id_cita = c.id_cita LIMIT 1);
