package com.example.demo.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.IMembreServices;
import com.example.demo.entities.*;
import org.springframework.web.multipart.MultipartFile;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@AllArgsConstructor
@CrossOrigin
public class MembreRestController {
	@Autowired
	IMembreServices iMembreServices;
	public static final String DIRECTORY = System.getProperty("user.home") + "/Downloads/";
	@PutMapping ("/upload/{id}")
	public ResponseEntity<List<String>> uploadFiles(@RequestParam("files")List<MultipartFile> multipartFiles,@PathVariable Long id) throws IOException {
		List<String> filenames = new ArrayList<>();
		for(MultipartFile file : multipartFiles) {
			String filename = StringUtils.cleanPath(file.getOriginalFilename());
			Path fileStorage = get(DIRECTORY, filename).toAbsolutePath().normalize();
			copy(file.getInputStream(), fileStorage, REPLACE_EXISTING);
			filenames.add(filename);
			Membre membreAediter=iMembreServices.findMember(id) ;

			membreAediter.setCv(filename);

			Membre finalMembre=iMembreServices.updateMember(membreAediter);
		}
		return ResponseEntity.ok().body(filenames);
	}

	@GetMapping("download/{filename}")
	public ResponseEntity<Resource> downloadFiles(@PathVariable("filename") String filename) throws IOException {
		Path filePath = get(DIRECTORY).toAbsolutePath().normalize().resolve(filename);
		if(!Files.exists(filePath)) {
			throw new FileNotFoundException(filename + " was not found on the server");
		}
		Resource resource = new UrlResource(filePath.toUri());
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("File-Name", filename);
		httpHeaders.add(CONTENT_DISPOSITION, "attachment;File-Name=" + resource.getFilename());
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
				.headers(httpHeaders).body(resource);
	}



	@RequestMapping(value = "/membres", method = RequestMethod.GET)
	public List<Membre> findAll() {
		return iMembreServices.findAll();

	}

	@RequestMapping(value = "/membre/{id}", method = RequestMethod.GET)
	public Membre findMembres(@PathVariable Long id) {
		return iMembreServices.findMember(id);

	}

	@GetMapping(value = "/membre/search/cin")
	public Membre findOneMemberByCin(@RequestParam String cin)

	{
		return iMembreServices.findByCin(cin);
	}

	@GetMapping(value = "/membre/search/email")

	public Membre findOneMemberByEmail(@RequestParam String email)

	{
		return iMembreServices.findByEmail(email);
	}

	
	@PostMapping(value = "/membres/enseignant")

	public Membre addMembre(@RequestBody EnseignantChercheur m)

	{
		return iMembreServices.addMember(m);

	}

	@PostMapping(value = "/membres/etudiant")

	public Membre addMembre(@RequestBody Etudiant e)

	{
		return iMembreServices.addMember(e);
	}
	@PutMapping(value="/membres/etudiant/{id}")
	public Membre updatemembre(@PathVariable Long id, @RequestBody Etudiant p)
	{
		p.setId(id);
		return iMembreServices.updateMember(p);
	}

	@PutMapping(value="/membres/enseignant/{id}")
	public Membre updateMembreEnsg(@PathVariable Long id, @RequestBody EnseignantChercheur p)
	{
		p.setId(id);
	       return iMembreServices.updateMember(p);
	}
	@PutMapping(value="/membres/etudiant")
	public Etudiant affecter(@RequestParam Long idetd , @RequestParam Long idens )
	{
		
	       return iMembreServices.affecterEtudiantToEnseignant(idetd, idens);
	}
	@DeleteMapping(value="/membres/{id}")
	public void deleteMembre(@PathVariable Long id)
	{
		iMembreServices.deleteMember(id);
	}
	
	@GetMapping("/fullmemberPublications/{id}")
	
	public Membre findFullMember(@PathVariable(name="id") Long id)	{

	Membre mbr =iMembreServices.findMember(id);
	mbr.setPubs(iMembreServices.findPublicationparauteur(id));
	return mbr;
	}

	@GetMapping("/membersOutil/{id}")
	
	public Membre findFullMemberOutils(@PathVariable(name="id") Long id)	{

	Membre mbr =iMembreServices.findMember(id);
	mbr.setOutils(iMembreServices.findOutilparauteur(id));
	return mbr;
	}

	@GetMapping("/membersEvents/{id}")
	
	public Membre findFullMemberEvents(@PathVariable(name="id") Long id)	{

	Membre mbr =iMembreServices.findMember(id);
	mbr.setEvents(iMembreServices.findEvenementparauteur(id));
	return mbr;
	}

}
