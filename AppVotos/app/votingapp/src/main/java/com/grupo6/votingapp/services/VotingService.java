package com.grupo6.votingapp.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.internal.util.collections.IdentitySet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6.votingapp.dtos.stats.QuestionStats;
import com.grupo6.votingapp.dtos.stats.UserSelectedOptions;
import com.grupo6.votingapp.dtos.stats.UserStats;
import com.grupo6.votingapp.dtos.stats.VotingStatsDTO;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.dtos.votes.CreateVoteDTO;
import com.grupo6.votingapp.dtos.votes.VoteWithNoRelationsDTO;
import com.grupo6.votingapp.dtos.votings.RegisterVotingDTO;
import com.grupo6.votingapp.dtos.votings.UpdateVotingDTO;
import com.grupo6.votingapp.dtos.votings.VotingNoRelationsVotesCountDTO;
import com.grupo6.votingapp.dtos.votings.VotingWithNoCreatorDTO;
import com.grupo6.votingapp.dtos.votings.VotingWithNoRelationsDTO;
import com.grupo6.votingapp.exceptions.authentication.UnauthorizedException;
import com.grupo6.votingapp.exceptions.votingservice.UserAlreadyVotedException;
import com.grupo6.votingapp.models.Option;
import com.grupo6.votingapp.models.Question;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.models.Vote;
import com.grupo6.votingapp.models.VotesQuestionsOptions;
import com.grupo6.votingapp.models.VotesQuestionsOptionsId;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.repositories.OptionRepository;
import com.grupo6.votingapp.repositories.QuestionRepository;
import com.grupo6.votingapp.repositories.StatsRepository;
import com.grupo6.votingapp.repositories.UserRepository;
import com.grupo6.votingapp.repositories.VoteRepository;
import com.grupo6.votingapp.repositories.VotingRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VotingService {
    private ImageService imageService;
    private UserService userService;

    private VotingRepository votingRepository;
    private VoteRepository  voteRepository;
    private QuestionRepository questionRepository;
    private OptionRepository optionRepository;
    private UserRepository userRepository;
    private StatsRepository statsRepository;
    
    //* Verificar se um user já votou numa votação
    private boolean userAlreadyVoted(Long votingId, Long userId) {
        return voteRepository.findVoteByVoterIdAndVotingId(userId, votingId).isPresent();
    }

    //* Obter uma votação específica a que o user tem acesso
    public VotingWithNoCreatorDTO getAccessibleVotingToUser(String votingId, String userId) throws UnauthorizedException{ //* Parece funcionar
        Voting voting = votingRepository.findAccessibleVotingToUser(userId, Long.parseLong(votingId)).orElse(null);
        if(voting == null){
            throw new UnauthorizedException("User with id '"+ userId + "' does not have access to a voting with id '" + votingId + "'!" );
        }
        VotingWithNoCreatorDTO result = new VotingWithNoCreatorDTO(voting);
        boolean userAlreadyVoted = userAlreadyVoted(Long.parseLong(votingId), Long.parseLong(userId));
        result.setUseralreadyvoted(userAlreadyVoted);
        result.setCreator(new UsersWithNoRelationsDTO(voting.getCreator()));
        return result;
    }

    //* Obter todas as votações a que o user tem acesso bem como o número de páginas que o conjunto de todas essas votações possui
    public Map<String,Object> getAccessibleVotingsToUser(String userId, String term, String orderBy, String order, int pageNumber, int pageSize){
        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy);
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize, sort);

        Page<Voting> votingsPage;
        if (orderBy.equals("votes")) {//* O campo votes não existe na base de dados, pelo que a ordenação tem de ser calculada de uma forma especial
            pageable = PageRequest.of(pageNumber-1, pageSize);
            if (order.equals("desc")) {
                votingsPage = votingRepository.findAccessibleVotingsToUserOrderByVotesDesc(userId, term, pageable);
            } else {
                votingsPage = votingRepository.findAccessibleVotingsToUserOrderByVotesAsc(userId, term, pageable);
            }
        } else {
            votingsPage = votingRepository.findAccessibleVotingsToUser(userId, term, pageable);
        }
        List<Voting> votings = votingsPage.getContent();
        
        List<VotingWithNoCreatorDTO> votingsWithNoRelations = votings.stream()
        .map(voting -> {
            VotingWithNoCreatorDTO votingWithNoCreatorDTO = new VotingWithNoCreatorDTO(voting);
            boolean userAlreadyVoted = userAlreadyVoted(voting.getId(), Long.parseLong(userId)); //! Tentar ver se dá para fazer isto numa só query.
            votingWithNoCreatorDTO.setUseralreadyvoted(userAlreadyVoted);
            votingWithNoCreatorDTO.setCreator(new UsersWithNoRelationsDTO(voting.getCreator()));
            return votingWithNoCreatorDTO;
        }).toList();
        
        return Map.of("votings", votingsWithNoRelations, "totalPages", votingsPage.getTotalPages());
    }

    //* Função auxiliar que converte string para java.util.Date
    private Date stringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(date);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing date string to Date object");
        }
    }

    //* Função auxiliar que filtra as votações de acordo com os parâmetros passados
    private List<Voting> filterVotings(List<Voting> votings, String enddate_start, String enddate_end, String creationdate_start, String creationdate_end, String privatevoting, String in_progress_filter) {
        if(!enddate_start.isEmpty()) {
            Date enddateStart = stringToDate(enddate_start);
            votings = votings.stream().filter(voting -> {
                if(voting.getEnddate() == null) return true;
                return voting.getEnddate().equals(enddateStart) || voting.getEnddate().after(enddateStart);
            }).collect(Collectors.toList());
        }
        if(!enddate_end.isEmpty()) {
            Date enddateEnd = stringToDate(enddate_end);
            votings = votings.stream().filter(voting -> {
                if(voting.getEnddate() == null) return false;
                return voting.getEnddate().equals(enddateEnd) || voting.getEnddate().before(enddateEnd);
            }).collect(Collectors.toList());
        }
        if(!creationdate_start.isEmpty()) {
            Date creationdateStart = stringToDate(creationdate_start);
            votings = votings.stream().filter(voting -> voting.getCreationdate().equals(creationdateStart) || 
                                                        voting.getCreationdate().after(creationdateStart)).collect(Collectors.toList());
        }
        if(!creationdate_end.isEmpty()) {
            Date creationdateEnd = stringToDate(creationdate_end);
            votings = votings.stream().filter(voting -> voting.getCreationdate().equals(creationdateEnd) || 
                                                        voting.getCreationdate().before(creationdateEnd)).collect(Collectors.toList());
        }
        if(!privatevoting.isEmpty()) {
            boolean privateVoting = Boolean.parseBoolean(privatevoting);
            votings = votings.stream().filter(voting -> voting.isPrivatevoting() == privateVoting).collect(Collectors.toList());
        }

        Date now = new Date();
        if(!in_progress_filter.isEmpty()){
            boolean inProgress = Boolean.parseBoolean(in_progress_filter);
            votings = votings.stream().filter(voting -> {
                if (voting.getEnddate() == null) {return inProgress;} // true
                else if(voting.getEnddate().after(now)) return inProgress; // true
                else return !inProgress; // false
            }).collect(Collectors.toList());
        }
        return votings;
    }

    //* Obter o histórico de votações de um user
    public Map<String,Object> getUserVotingHistory(String userId, String enddate_start, String enddate_end, String creationdate_start, String creationdate_end, String privatevoting, String in_progress_filter, String orderBy, String order, int pageNumber, int pageSize) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy.equals("votes") ? "enddate" : orderBy);
        List<Voting> votingsList = votingRepository.findUserVotingHistory(userId, sort);
        votingsList = filterVotings(votingsList, enddate_start, enddate_end, creationdate_start, creationdate_end, privatevoting, in_progress_filter);
        List<Long> votingIds = votingsList.stream().map(Voting::getId).toList();
        Map<Long, Long> votesCounts = statsRepository.getCountVotesOfVotings(votingIds);

        List<VotingWithNoRelationsDTO> votings = votingsList.stream()
        .map(voting -> {
            Long votesCount = votesCounts.getOrDefault(voting.getId(), 0L);
            VotingWithNoRelationsDTO votingWithNoRelationsDTO = new VotingNoRelationsVotesCountDTO(voting, votesCount);
            votingWithNoRelationsDTO.setUseralreadyvoted(true);
            return votingWithNoRelationsDTO;
        }).collect(Collectors.toList());

        if(orderBy.equals("votes")) {
            votings.sort((v1, v2) -> {
                if(order.equals("desc")) {
                    return Long.compare(votesCounts.getOrDefault(v2.getId(), 0L), votesCounts.getOrDefault(v1.getId(), 0L));
                } else {
                    return Long.compare(votesCounts.getOrDefault(v1.getId(), 0L), votesCounts.getOrDefault(v2.getId(), 0L));
                }
            });
        }

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Page<VotingWithNoRelationsDTO> votingsPage = PaginationHelper.paginate(votings, pageable);

        return Map.of("votings", votingsPage.getContent(), "totalPages", votingsPage.getTotalPages());
    }

    //* Obter todas as votações criadas por um user
    public Map<String,Object> getVotingsFromCreatorId(String userId, String enddate_start, String enddate_end,String creationdate_start, String creationdate_end, String privatevoting, String in_progress_filter, String orderBy, String order, int pageNumber, int pageSize){
        Sort sort = Sort.by(Sort.Direction.fromString(order), orderBy.equals("votes") ? "enddate" : orderBy);
        List<Voting> votingsList = votingRepository.findByUserId(userId, sort);
        votingsList = filterVotings(votingsList, enddate_start, enddate_end, creationdate_start, creationdate_end, privatevoting, in_progress_filter);
        List<Long> votingIds = votingsList.stream().map(Voting::getId).toList();
        Map<Long, Long> votesCounts = statsRepository.getCountVotesOfVotings(votingIds);

        List<VotingWithNoRelationsDTO> votings = votingsList.stream()
        .map(voting -> {
            Long votesCount = votesCounts.getOrDefault(voting.getId(), 0L);
            VotingWithNoRelationsDTO votingWithNoRelationsDTO = new VotingNoRelationsVotesCountDTO(voting, votesCount);
            boolean userAlreadyVoted = userAlreadyVoted(voting.getId(), Long.parseLong(userId));
            votingWithNoRelationsDTO.setUseralreadyvoted(userAlreadyVoted);
            return votingWithNoRelationsDTO;
        }).collect(Collectors.toList());

        if(orderBy.equals("votes")) {
            votings.sort((v1, v2) -> {
                if(order.equals("desc")) {
                    return Long.compare(votesCounts.getOrDefault(v2.getId(), 0L), votesCounts.getOrDefault(v1.getId(), 0L));
                } else {
                    return Long.compare(votesCounts.getOrDefault(v1.getId(), 0L), votesCounts.getOrDefault(v2.getId(), 0L));
                }
            });
        }

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Page<VotingWithNoRelationsDTO> votingsPage = PaginationHelper.paginate(votings, pageable);

        return Map.of("votings", votingsPage.getContent(), "totalPages", votingsPage.getTotalPages());
    }

    //* Obter as estatísticas de uma votação
    public VotingStatsDTO getVotingStats(String userId, String votingId) {
        Voting voting = votingRepository.findAccessibleVotingToUser(userId, Long.parseLong(votingId)).orElse(null);
        if(voting == null){//* O user tem acesso à votação?
            throw new UnauthorizedException("User with id '"+ userId + "' does not have access to a voting with id '" + votingId + "'!" );
        }
        //* Verificar se o user tem permissão para ver as estatísticas da votação.
        boolean allowedToSeeStats = false;
        if(voting.getCreator().getId().equals(Long.parseLong(userId))){//* O user é o criador da votação?
            allowedToSeeStats = true;
        } else if (voting.isShowstats()) {
            Date now = new Date();
            Date enddate = voting.getEnddate();
            boolean active = enddate == null || now.before(enddate);
            if(active) allowedToSeeStats = voting.isShowstatsrealtime();
            else allowedToSeeStats = true;
        }
        if(!allowedToSeeStats){
            throw new UnauthorizedException("User with id '"+ userId + "' does not have permission to see the stats of the voting with id '" + votingId + "'!" );
        }

        Long countVotesOfVoting = statsRepository.getCountVotesOfVoting(votingId);
        List<QuestionStats> questionsStats = statsRepository.getQuestionStats(votingId);
        List<UserStats> users = statsRepository.getUsersOfVoting(votingId);
        List<UserSelectedOptions> userSelectedOptions = statsRepository.getUsersSelectedOptions(votingId);
        return new VotingStatsDTO(countVotesOfVoting, questionsStats, users, userSelectedOptions);
    }

    //* Criar uma nova votação
    public Voting createVoting(RegisterVotingDTO newVoting, List<MultipartFile> images, String userId) throws Exception {
        newVoting.setCreationdate(new Date());
        Voting voting = newVoting.toEntity();

        Map<String, String> uploadedImages = new HashMap<>();

        List<String> privateVotersIds = newVoting.getPrivateSelectedUsers();
        if(newVoting.isPrivatevoting() && privateVotersIds != null && !privateVotersIds.isEmpty()) {
            List<Long> privateVotersIdsLong = privateVotersIds.stream().map(Long::parseLong).toList();
            List<User> privateVoters = userService.getUsersByIds(privateVotersIdsLong);
            voting.setPrivatevoters(privateVoters);
        }
            
        if(images != null && !images.isEmpty()){
            uploadedImages = imageService.uploadImages(images);
            voting.setImage(uploadedImages.getOrDefault(newVoting.getImage(), null));

            for (Question question : voting.getQuestions()) {
                for(Option option : question.getOptions()) {
                    option.setImage(uploadedImages.getOrDefault(option.getImage(), null));
                }
            }
        }
        // catch (Exception e) {
        //     for(String uploadedImageName : uploadedImages.values()) {
        //         imageService.deleteFile(uploadedImageName);
        //     }
        // }

        User dummyUser = new User(); //* Este dummyUser serve só para a nova votação ter informação do id do seu criador.
        dummyUser.setId(Long.parseLong(userId)); //* mete o id do criador no objecto dummyUser
        voting.setCreator(dummyUser); //* para ser associado ao seu user criador na base de dados
        return votingRepository.save(voting);//* Guarda a votação na base de dados
    }

    //* Actualizar uma votação
    public VotingWithNoRelationsDTO updateVoting(UpdateVotingDTO updatedVoting, String votingId, String userId){
        Voting voting = votingRepository.findVotingByCreatorId(Long.parseLong(votingId), userId).orElse(null);
        if(voting == null){
            throw new UnauthorizedException("User with id '"+ userId + "' can not edit voting with id '" + votingId + "'!" );
        } else {
            updatedVoting.updateVotingFromDTO(voting);
            Voting updatedVotingEntity = votingRepository.save(voting);
            return new VotingWithNoRelationsDTO(updatedVotingEntity);
        }
    }

    //* Apagar uma votação
    public void deleteVoting(String votingId, String userId) throws UnauthorizedException{
        Voting voting = votingRepository.findVotingByCreatorId(Long.parseLong(votingId), userId).orElse(null);
        if(voting == null){
            throw new UnauthorizedException("User with id '"+ userId + "' can not delete voting with id '" + votingId + "'!" );
        }
        
        //* Elimina os votantes privados associados à votação
        voting.setPrivatevoters(List.of());
        voting = votingRepository.save(voting);
        //* Elimina as imagens associadas à votação
        if (voting.getImage() != null){
            imageService.deleteFile(voting.getImage());
        }
        for (Question question : voting.getQuestions()) {
            for(Option option : question.getOptions()) {
                if (option.getImage() != null){
                    imageService.deleteFile(option.getImage());
                }
            }
        }
        //* Elimina os votos associados à votação
        voting.getVotes().forEach(voteRepository::delete);
        
        votingRepository.delete(voting);
    }
    
    //* Submeter um voto
    public VoteWithNoRelationsDTO createVote(CreateVoteDTO voteDto, String userId) throws UnauthorizedException, UserAlreadyVotedException{
        VotingWithNoCreatorDTO voting = getAccessibleVotingToUser(voteDto.getVotingid(), userId);
        if(voting == null){
            throw new UnauthorizedException("Voting not found or not accessible to user!");
        }
        if(userAlreadyVoted(voting.getId(), Long.parseLong(userId))){
            throw new UserAlreadyVotedException("User already voted in this voting!");
        }

        Vote registeredVote = saveVote(voteDto, userId);
        return new VoteWithNoRelationsDTO(registeredVote);
    }

    //* Guardar um voto
    private Vote saveVote(CreateVoteDTO voteDto, String userId) {
        Vote vote = new Vote();
        //* Associa o voto ao user que vota e à votação 
        User voter = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        vote.setVoter(voter);
        Voting voting = votingRepository.findById(Long.parseLong(voteDto.getVotingid())).orElseThrow(() -> new RuntimeException("Voting not found"));
        vote.setVoting(voting);
        //* Obtém as opções selecionadas pelo user em cada pergunta
        Map<String,List<String>> selectedOptions = voteDto.getOptions(); //* {questionId: [optionId1, optionId2], ...}
        Set<VotesQuestionsOptions> votesQuestionsOptions = new IdentitySet<>();

        //* Para cada pergunta, associa as opções selecionadas à respectiva questão e ao voto. (relação ternária)
        for (Map.Entry<String,List<String>> entry : selectedOptions.entrySet()) {
            String questionId = entry.getKey();
            List<String> optionIds = entry.getValue();
            Question question = questionRepository.findById(Long.parseLong(questionId)).orElseThrow(() -> new RuntimeException("Question not found"));
            //* Para cada opção selecionada, associa a opção à questão e ao voto
            for (String optionId : optionIds) {
                VotesQuestionsOptions voteQuestionOption = new VotesQuestionsOptions();
                voteQuestionOption.setVote(vote);
                voteQuestionOption.setQuestion(question);
                Option option = optionRepository.findById(Long.parseLong(optionId)).orElseThrow(() -> new RuntimeException("Option not found"));
                voteQuestionOption.setOption(option);
                voteQuestionOption.setId(new VotesQuestionsOptionsId(vote.getId(), question.getId(), option.getId()));
                votesQuestionsOptions.add(voteQuestionOption);
            }
        }
        vote.setVotesQuestionsOptions(votesQuestionsOptions);
        return voteRepository.save(vote);
    }

    //* Obtém o id de uma votação que contenha uma imagem específica
    public Long votingIdWithImage(String userId, String imageName) {
        return votingRepository.votingWithImage(userId, imageName).orElse(null);
    }
}
