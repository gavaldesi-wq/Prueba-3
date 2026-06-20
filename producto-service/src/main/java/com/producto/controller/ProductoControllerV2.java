package com.producto.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.producto.assemblers.ProductoModelAssembler;
import com.producto.DTO.ProductoDTO;
import com.producto.service.ProductoService;

@RestController
@RequestMapping("productos/v2")
public class ProductoControllerV2 {
    private final ProductoService productoService;
    private final ProductoModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(ProductoControllerV2.class);

    public ProductoControllerV2(ProductoService productoService, ProductoModelAssembler assembler) {
        this.productoService = productoService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<ProductoDTO>> listarProductos() {
        logger.info("V2 GET /productos - Listando productos");
        List<EntityModel<ProductoDTO>> productos = productoService.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(productos, linkTo(methodOn(ProductoControllerV2.class).listarProductos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<ProductoDTO> obtenerProducto(@PathVariable Long id) {
        logger.info("V2 GET /productos/{} - Obteniendo producto", id);
        ProductoDTO producto = productoService.getById(id);
        return assembler.toModel(producto);
    }

    @GetMapping("/nombre/{nombre}")
    public EntityModel<ProductoDTO> obtenerProductoPorNombre(@PathVariable String nombre) {
        logger.info("V2 GET /productos/nombre/{} - Obteniendo producto por nombre", nombre);
        ProductoDTO producto = productoService.getByNombre(nombre);
        return assembler.toModel(producto);
    }
}
