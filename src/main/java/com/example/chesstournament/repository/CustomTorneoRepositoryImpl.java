package com.example.chesstournament.repository;

import com.example.chesstournament.dto.TorneoRicercaDTO;
import com.example.chesstournament.model.Torneo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTorneoRepositoryImpl implements CustomTorneoRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Torneo> findByExample(TorneoRicercaDTO example) {
        Map<String, Object> paramaterMap = new HashMap<String, Object>();
        List<String> whereClauses = new ArrayList<String>();

        StringBuilder queryBuilder = new StringBuilder("select t from Torneo t where t.id = t.id ");

        if(StringUtils.isNotEmpty(example.getDenominazione())){
            whereClauses.add("t.denominazione like :denominazione");
            paramaterMap.put("denominazione", "%" + example.getDenominazione() + "%");
        }
        if(example.getStato() != null){
            whereClauses.add("t.stato = :stato");
            paramaterMap.put("stato", example.getStato() );
        }
        if(example.getEloMinimo() != null){
            whereClauses.add("t.eloMinimo <= :eloMinimo");
            paramaterMap.put("eloMinimo", example.getEloMinimo());
        }
        if(example.getUtenteCreazioneId() != null){
            whereClauses.add("t.utenteCreazione.id = :utentecreazione");
            paramaterMap.put("utenteCreazione", example.getUtenteCreazioneId());
        }
        if(example.getQuotaIscrizione() != null){
            whereClauses.add("t.quotaIscrizione <= :quotaIscrizione");
            paramaterMap.put("quotaIscrizione", example.getQuotaIscrizione());
        }

        queryBuilder.append(!whereClauses.isEmpty()?" and ":"");
        queryBuilder.append(StringUtils.join(whereClauses, " and "));
        TypedQuery<Torneo> typedQuery = entityManager.createQuery(queryBuilder.toString(), Torneo.class);

        for (String key : paramaterMap.keySet()) {
            typedQuery.setParameter(key, paramaterMap.get(key));
        }

        return typedQuery.getResultList();
    }
}
