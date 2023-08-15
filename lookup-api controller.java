lookup-api changes
Controller.java

package com.deanhealth.lookupapi.controller;

import com.deanhealth.lookupapi.model.*;
import com.deanhealth.lookupapi.service.CodesetService;
import com.deanhealth.lookupapi.service.XRefService;
import com.deanhealth.lookupapi.util.Constants;
import com.deanhealth.lookupapi.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class Controller {

    @Value("${codeset.eds.name.degree}")
    private String degreeCodesetName;

    @Value("${codeset.eds.name.specialty}")
    private String specialtyCodesetName;

    @Value("${codeset.eds.name.county}")
    private String countyCodesetName;

    @Value("${xref.eds.type.specialty}")
    private String specialtyXRefType;
    @Value("${xref.eds.type.practitioner-specialty}")
    private String practitionerSpecialtyXRefType;

    @Value("${xref.eds.type.eds-practitioner-specialty}")
    private String edsPractitionerSpecialtyXRefType;

    @Value("${xref.eds.type.language}")
    private String languageXRefType;

    @Value("${xref.eds.type.degree}")
    private String degreeXRefType;

    @Autowired
    private CodesetService codesetService;

    @Autowired
    private XRefService xRefService;

    @GetMapping("/codeset/specialty")
    public CodesetRef getSpecialtyCodeset(@RequestParam String srcSysCode, @RequestParam String code) {
        return codesetService.getCodeset(this.trimAndUppercase(srcSysCode), specialtyCodesetName, code, Boolean.FALSE);
    }

    @GetMapping("/codeset/degree")
    public CodesetRef getDegreeCodeset(@RequestParam String srcSysCode, @RequestParam String code) {
        return codesetService.getCodeset(this.trimAndUppercase(srcSysCode), degreeCodesetName, code, Boolean.FALSE);
    }
    @GetMapping("/codeset/county")
    public CodesetRef getCountyCodeset(@RequestParam String srcSysCode, @RequestParam String code) {
        return codesetService.getCodeset(this.trimAndUppercase(srcSysCode), countyCodesetName, code, Boolean.FALSE);
    }
    @GetMapping("/codeset/mtv-eds/specialty/{code}")
    public CodesetRef getMtvSpecialtyCodeset( @PathVariable String code) {
        return codesetService.getActiveMtvCodeset(specialtyCodesetName, code);
    }

    @GetMapping("/codeset/mtv-eds/county/{code}")
    public CodesetRef getMtvCountyCodeset( @PathVariable String code) {
        return codesetService.getActiveMtvCodeset(countyCodesetName, code);
    }

    @GetMapping("/xref/mtv-eds/specialty/{code}")
    public XRefDto getEdsSpecialty(@PathVariable("code") String code) {
        return xRefService.mapXRefToXRefDto(xRefService.getXRef(specialtyXRefType, Constants.MTV, Constants.EDS, Constants.XREF_CD_TYPE_SPECIALTY_CD, code));
    }

    @GetMapping("/xref/language")
    public XRefDto getEdsLanguage(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd, @RequestParam String sourceValue1) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        return xRefService.mapXRefToXRefDto(xRefService.getXRef(languageXRefType, trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_LANGUAGE, sourceValue1));
    }

    @GetMapping("/xref/degree")
    public XRefDto getDegree(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd, @RequestParam String sourceValue1) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        return xRefService.mapXRefToXRefDto(xRefService.getXRef(degreeXRefType, trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_DEGREE, sourceValue1));
    }

    @GetMapping("/xref/specialty")
    public XRefDto getSpecialty(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd, @RequestParam String sourceValue1) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        return xRefService.mapXRefToXRefDto(xRefService.getXRef(specialtyXRefType, trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_SPECIALTY_CD, sourceValue1));
    }

    @GetMapping("/xref/practitioner-specialty")
    public XRefDto getPractitionerSpecialty(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd, @RequestParam String sourceValue1) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        return xRefService.mapXRefToXRefDto(xRefService.getXRef(practitionerSpecialtyXRefType, trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_SPECIALTY_CD, sourceValue1));
    }

    @GetMapping("/xref/eds-practitioner-specialty")
    public XRefDto getEdsPractitionerSpecialty(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd, @RequestParam String sourceValue1) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        return xRefService.mapXRefToXRefDto(xRefService.getXRef(edsPractitionerSpecialtyXRefType, trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_DEGREE_N_SPEC, sourceValue1));
    }

    @GetMapping("/xref/bulk-lookup")
    public LookUpBulkResponse getBulkXrefs(@RequestBody LookUpBulkRequest request) {
        return this.xRefService.getBulkXRefs(request);
    }
    @GetMapping("/xref/degrees")
    public List<XRefDto> getDegrees(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        List<XRef> resultList= xRefService.getXRefs(degreeXRefType, trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_DEGREE);
        return Objects.isNull(resultList) || ( resultList.isEmpty()) ? new ArrayList() : resultList.stream().map(xRefDto -> xRefService.mapXRefToXRefDto(xRefDto)).collect(Collectors.toList());

    }
    @GetMapping("/xref/languages")
    public List<XRefDto> getLanguages(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        List<XRef> resultList= xRefService.getXRefs(languageXRefType, trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_LANGUAGE);
        return Objects.isNull(resultList) || ( resultList.isEmpty()) ? new ArrayList() : resultList.stream().map(xRefDto -> xRefService.mapXRefToXRefDto(xRefDto)).collect(Collectors.toList());
    }
    @GetMapping("/xref/specialties")
    public List<XRefDto> getSpecialties(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        List<XRef> resultList= xRefService.getXRefs(specialtyXRefType, this.trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_SPECIALTY_CD);
        return Objects.isNull(resultList) || ( resultList.isEmpty()) ? new ArrayList() : resultList.stream().map(xRefDto -> xRefService.mapXRefToXRefDto(xRefDto)).collect(Collectors.toList());
    }
    @GetMapping("/xref/practitioner-specialties")
    public List<XRefDto> getPractitionerSpecialties(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        List<XRef> resultList= xRefService.getXRefs(practitionerSpecialtyXRefType, this.trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_SPECIALTY_CD);
        return Objects.isNull(resultList) || ( resultList.isEmpty()) ? new ArrayList() : resultList.stream().map(xRefDto -> xRefService.mapXRefToXRefDto(xRefDto)).collect(Collectors.toList());
    }

    @GetMapping("/xref/eds-practitioner-specialties")
    public List<XRefDto> getEdsPractitionerSpecialties(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);

        List<XRef> resultList= xRefService.getXRefs(edsPractitionerSpecialtyXRefType, this.trimAndUppercase(sourceSystemCd), destinationSystemCd, Constants.XREF_CD_TYPE_DEGREE_N_SPEC);
        return Objects.isNull(resultList) || ( resultList.isEmpty()) ? new ArrayList() : resultList.stream().map(xRefDto -> xRefService.mapXRefToXRefDto(xRefDto)).collect(Collectors.toList());
    }

    @PostMapping("/codeset/counties")
    public LookUpBulkResponse getBulkCodesets(@RequestBody LookUpBulkRequest request) {
        LookUpBulkResponse addCodeSetRef= new LookUpBulkResponse();
        addCodeSetRef.addCodeSetRef(codesetService.getCodesets(Constants.MTV, countyCodesetName, request.getCountyCodes(), Boolean.FALSE));
        return addCodeSetRef;
    }
    @GetMapping("/codeset/counties")
    public List<CodesetRef> getCodesetsCounties(@RequestParam String sourceSystemCd) {
        return codesetService.getCodeset(this.trimAndUppercase(sourceSystemCd), countyCodesetName, Boolean.FALSE);
    }
    @GetMapping("/v1/codesets")
    public List<CodesetRef> getCodesets(@RequestParam String sourceSystemCd, @RequestParam String codesetName) {
        return codesetService.getCodeset(this.trimAndUppercase(sourceSystemCd), codesetName, Boolean.FALSE);
    }
    @GetMapping("/v1/codeset")
    public CodesetRef getCodeset(@RequestParam String sourceSystemCd, @RequestParam String codesetName, @RequestParam String code) {
        return codesetService.getCodeset(this.trimAndUppercase(sourceSystemCd), codesetName, code, Boolean.FALSE);
    }
    @GetMapping("/v1/xrefs")
    public List<XRefDto> getXrefs(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd, @RequestParam String xrefType , @RequestParam String sourceValue1Type) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        List<XRef> resultList= xRefService.getXRefs(xrefType, this.trimAndUppercase(sourceSystemCd), destinationSystemCd, sourceValue1Type);
        return Objects.isNull(resultList) || ( resultList.isEmpty()) ? new ArrayList() : resultList.stream().map(xRefDto -> xRefService.mapXRefToXRefDto(xRefDto)).collect(Collectors.toList());
    }
    @GetMapping("/v1/xref")
    public XRefDto getXRef(@RequestParam String sourceSystemCd, @RequestParam String destinationSystemCd, @RequestParam String xrefType , @RequestParam String sourceValue1Type, @RequestParam String sourceValue1) {
        destinationSystemCd= Util.upperCaseOrNull(destinationSystemCd);
        sourceSystemCd= Util.upperCaseOrNull(sourceSystemCd);
        return xRefService.mapXRefToXRefDto(xRefService.getXRef(xrefType, trimAndUppercase(sourceSystemCd), destinationSystemCd, sourceValue1Type, sourceValue1));
    }
    private String trimAndUppercase(String str) {
        if(Objects.isNull(str))
            return null;
        return str.trim().toUpperCase();
    }
}
