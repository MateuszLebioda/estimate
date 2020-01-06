package com.estimate.services;

import com.estimate.dao.services.dao.JobTemplateAbstractMaterialDao;
import com.estimate.dao.services.dao.JobTemplateDao;
import com.estimate.model.entities.JobTemplate;
import com.estimate.model.entities.JobTemplateAbstractMaterial;
import com.estimate.model.entities.User;
import com.estimate.model.entities.dto.JobTemplateDTO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless(name = "jobTemplateServiceImpl")
public class JobTemplateServiceImpl implements JobTemplateService {

    @Inject
    private Optional<User> user;

    @EJB
    private JobTemplateAbstractMaterialDao jobTemplateAbstractMaterialDao;

    @EJB
    private DTOConverter DTOConverter;

    @EJB
    private JobTemplateDao jobTemplateDao;

    @Override
    public Long addJobTemplateDTO(JobTemplateDTO jobTemplateDTO) {
        JobTemplate jobTemplate = DTOConverter.makeJobTemplateFromDTO(jobTemplateDTO);
        Long id = jobTemplateDao.save(jobTemplate).getId();
        Set<JobTemplateAbstractMaterial> materials =
                jobTemplateDTO.getMaterials().stream()
                        .map(DTOConverter::makeJobTemplateAbstractMaterial).collect(Collectors.toSet());
        for (JobTemplateAbstractMaterial material : materials) {
            material.setJobTemplate(jobTemplate);
            jobTemplateAbstractMaterialDao.merge(material);
        }

        return id;
    }

    @Override
    public List<JobTemplateDTO> getAllJobTemples(User user) {

        return jobTemplateDao.getJobTemplatesByUser(user).stream().map(JobTemplate::toDto).collect(Collectors.toList());
    }

}
