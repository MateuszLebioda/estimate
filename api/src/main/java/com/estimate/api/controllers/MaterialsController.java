package com.estimate.api.controllers;

import com.estimate.model.entities.Material;
import com.estimate.model.entities.User;
import com.estimate.model.entities.Work;
import com.estimate.model.entities.dto.ClientDTO;
import com.estimate.model.entities.dto.MaterialDTO;
import com.estimate.model.entities.dto.WorkDTO;
import com.estimate.services.MaterialService;


import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/materials")
public class MaterialsController {

    @Inject
    private Optional<User> user;

    @EJB
    private MaterialService materialService;

    @POST
    @Path("/addMaterial")
    public Response addMaterial(MaterialDTO material){
        if(user.isPresent()){
            material.setUser(user.get());
            return Response.ok(materialService.addMaterialFromDTO(material)).build();
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/addWorks")
    public Response addMaterial(WorkDTO workDTO){
        if(user.isPresent()){
            workDTO.setUser(user.get());
            return Response.ok(materialService.addWorkFromDTO(workDTO)).build();
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }


    @GET
    @Path("/getAllMaterials")
    public Response getMaterials(){
        if(user.isPresent()){
            return Response.ok(materialService.getAllMaterials(user.get()).stream().map(Material::toDTO).collect(Collectors.toList())).build();
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Path("/getAllWorks")
    public Response getWorks(){
        if(user.isPresent()){
            return Response.ok(materialService.getAllWorks(user.get()).stream().map(Work::toDTO).collect(Collectors.toList())).build();
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @DELETE
    @Path("/deleteMaterial/{id}")
    public Response deleteMaterial(@PathParam("id") long id){
        if(user.isPresent()){
            Optional<Material> optionalMaterial = materialService.getMaterialById(id);
            if(optionalMaterial.isPresent()) {
                if (materialService.isMyMaterial(user.get(),optionalMaterial.get())) {
                    materialService.deleteMaterial(optionalMaterial.get());
                    return Response.ok().build();
                }else {
                    return Response.accepted("Client doest not exist").build();
                }
            }
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @DELETE
    @Path("/deleteWork/{id}")
    public Response deleteWork(@PathParam("id") long id){
        if(user.isPresent()){
            Optional<Work> optionalWork = materialService.getWorkById(id);
            if(optionalWork.isPresent()) {
                if (materialService.isMyMaterial(user.get(),optionalWork.get())) {
                    materialService.deleteWork(optionalWork.get());
                    return Response.ok().build();
                }else {
                    return Response.accepted("Client doest not exist").build();
                }
            }
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @PUT
    @Path("/updateMaterial")
    public Response updateClient(MaterialDTO materialDTO) {
        if(user.isPresent()){
            Material material = materialService.getMaterialById(materialDTO.getId()).get();
            if (materialService.isMyMaterial(user.get(), material)) {
                materialDTO.setUser(user.get());
                materialService.updateMaterial(material,materialDTO);
                return Response.ok().build();
            }else {
                return Response.accepted("Client doest not exist").build();
            }
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
