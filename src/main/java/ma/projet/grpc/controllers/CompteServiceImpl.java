package ma.projet.grpc.controllers;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ma.projet.grpc.services.CompteService;
import ma.projet.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {
    private final CompteService compteService;
    
    public CompteServiceImpl(CompteService compteService) {
        this.compteService = compteService;
    }
    
    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        var comptes = compteService.findAllComptes().stream()
        .map(compte -> Compte.newBuilder()
        .setId(compte.getId())
        .setSolde(compte.getSolde())
        .setDateCreation(compte.getDateCreation())
        .setType(TypeCompte.valueOf(compte.getType()))
        .build())
        .collect(Collectors.toList());
        
        responseObserver.onNext(GetAllComptesResponse.newBuilder()
        .addAllComptes(comptes).build());
        responseObserver.onCompleted();
    }
    
    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        try {
            var compte = compteService.findCompteById(request.getId());
            if (compte != null) {
                var grpcCompte = Compte.newBuilder()
                    .setId(compte.getId())
                    .setSolde(compte.getSolde())
                    .setDateCreation(compte.getDateCreation())
                    .setType(TypeCompte.valueOf(compte.getType()))
                    .build();
                responseObserver.onNext(GetCompteByIdResponse.newBuilder()
                    .setCompte(grpcCompte).build());
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Compte non trouvé avec l'ID: " + request.getId())
                    .asRuntimeException());
            }
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Type de compte invalide: " + e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                .withDescription("Erreur serveur: " + e.getMessage())
                .asRuntimeException());
        }
    }
    
    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        var comptes = compteService.findAllComptes();
        int count = comptes.size();
        float sum = 0;
        for (var compte : comptes) {
            sum += compte.getSolde();
        }
        float average = count > 0 ? sum / count : 0;
        
        SoldeStats stats = SoldeStats.newBuilder()
        .setCount(count)
        .setSum(sum)
        .setAverage(average)
        .build();
        
        responseObserver.onNext(GetTotalSoldeResponse.newBuilder().setStats(stats).build());
        responseObserver.onCompleted();
    }
    
    @Override
    public void saveCompte(SaveCompteRequest request, StreamObserver<SaveCompteResponse> responseObserver) {
        var compteReq = request.getCompte();
        var compte = new ma.projet.grpc.entities.Compte();
        compte.setSolde(compteReq.getSolde());
        compte.setDateCreation(compteReq.getDateCreation());
        compte.setType(compteReq.getType().name());
        
        var savedCompte = compteService.saveCompte(compte);
        
        var grpcCompte = Compte.newBuilder()
        .setId(savedCompte.getId())
        .setSolde(savedCompte.getSolde())
        .setDateCreation(savedCompte.getDateCreation())
        .setType(TypeCompte.valueOf(savedCompte.getType()))
        .build();
        
        responseObserver.onNext(SaveCompteResponse.newBuilder()
            .setCompte(grpcCompte).build());
        responseObserver.onCompleted();
    }
}
