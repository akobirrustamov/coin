package com.example.backend.Config;

import com.example.backend.Entity.*;
import com.example.backend.Enums.UserRoles;
import com.example.backend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class AutoRun implements CommandLineRunner {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final FacultyRepo facultyRepo;
    private final RankRepo rankRepo;
    private final CommanderRepo commanderRepo;
    private final TestSuitsidRepo testSuitsidRepo;
    @Override
    public void run(String... args) throws Exception {
        String adminPhone = "admin1234";
        if (roleRepo.findAll().isEmpty()) {
            saveRoles();
        }
        Optional<User> userByPhone = userRepo.findByPhone(adminPhone);
        saveUser(adminPhone, userByPhone);
        if (facultyRepo.findAll().isEmpty()) {
            saveFaculty();
        }
        if (rankRepo.findAll().isEmpty()) {
            saveRanks();
        }

        testSuitsidRepo.deleteAll();
       if ( testSuitsidRepo.findAll().isEmpty()) {
            saveTestSuitsid();
        }


    }

    private void saveTestSuitsid() {
        List<String> questions = List.of(
                "Men hayotda yaxshi orzular bilan yashayapman.",
                "Men juda omadsizman. Juda ko‘p muammolar qurshovida qolib ketayapman.",
                "Men juda omadli insonman, hamisha o‘zimni rivojlantirib boryapman.",
                "Negadir men tushkunlik holatiga tushib, qiynalayapman va menga hech kim yordam bermayapti.",
                "Hayotni sevish va undan zavqlanib yashash, o‘qish va ishlash kerak deb o‘ylayman.",
                "Ko‘pincha o‘zimni boshqara olmay kundan kunga asabiy holatlarga duch kelayapman.",
                "Kelajakdan umidim katta, barcha yaxshi tilaklarimni to‘laqonli amalga oshishiga ishonaman.",
                "Meni o‘z ichki muammolarim juda ko‘p qiynaydi. Ko‘pincha o‘zimni o‘zim boshqara olmay qolyapman.",
                "Men o‘z imkoniyatlarimga, hissiyotlarimga va barcha yaqinlarimga ishonaman, ular bilan baxtliman.",
                "Meni juda qiyin ahvolga solib kelayotgan hayotimdagi voqealar, yaqinlarimning meni mutlaqo tushunmasliklari va kutilmagan ko‘ngilsizliklar tufayli kundan kunga asabiylashib boryapman.",
                "Men kuchliman, hayotdagi har qanday qiyinchiliklarni yengib o‘tishga harakat qilyapman.",
                "Negadir hozirda men o‘zimdagi kuchsizlik va ojizlikni his qilyapman. Shu sabab ehtimol hayotga bo‘lgan qiziqishlarim so‘nib borayotganday go‘yo.",
                "Har bir inson o‘z hayotini, o‘z yaqinlarini, o‘z kasbini va o‘z-o‘zini sevib yashashi kerak deb o‘ylayman.",
                "Atrofimda va o‘z ichki kechinmalarimda yuz berayotgan o‘ta ziddiyatli holatlarni yengib o‘ta olmayotganligimni tan olish hissi meni havotirga solmoqda.",
                "Men hayotdan faxrlanib yashayman. O‘zimni yaxshi his qilyapman.",
                "Men hayotdan charchadim, muammolarim ko‘p. Shuning uchun o‘zimni yomon his qilyapman.",
                "Yaxshi niyatli insonlarga havasim keladi. Men ham ulardek bo‘lishni xohlayman.",
                "Ba’zi insonlarning muomalasi meni juda ham asabiylashtirmoqda va men ulardan nafratlanaman.",
                "Men har doim yaxshi kayfiyatda bo‘lishga harakat qilaman. Bunga juda ko‘p asoslar bor.",
                "Menga ba’zan bu dunyoni tark etish hissi bezovta qiladi va bu holat menda negadir ko‘proq uchrayapti."
        );

        for (int i = 0; i < questions.size(); i++) {
            String question = questions.get(i);
            int index = i + 1; // 1-based index

            int ball1 = (index % 2 == 0) ? 1 : 0;
            int ball2 = (index % 2 != 0) ? 1 : 0;

            TestSuitsid test = TestSuitsid.builder()
                    .testSuidsit(question)
                    .answer1("ha")
                    .ball1(ball1)
                    .answer2("yo‘q")
                    .ball2(ball2)
                    .build();

            testSuitsidRepo.save(test);
        }
    }



    private void saveRanks() {
        List<Rank> rankList = List.of(
                new Rank(1, "Rektor", LocalDateTime.now()),
                new Rank(2, "Ish boshqaruvchi", LocalDateTime.now()),
                new Rank(3, "Buxgalteriya", LocalDateTime.now()),
                new Rank(4, "Registrator ofisi", LocalDateTime.now()),
                new Rank(5, "Yoshlar masalalari, ma'naviyat va ma'rifat ishlari bo'yicha birinchi prorektor", LocalDateTime.now()),
                new Rank(6, "O'quv ishlari va ilmiy innovatsiyalar bo'yicha prorektor", LocalDateTime.now()),
                new Rank(7, "Kadrlar bo'limi", LocalDateTime.now()),
                new Rank(8, "Yoshlar bilan ishlash, ma'naviyat va ma'rifat bo'limi", LocalDateTime.now()),
                new Rank(9, "Doktorantura bo'limi", LocalDateTime.now()),
                new Rank(10, "Sirtqi bo'lim boshlig'i", LocalDateTime.now()),
                new Rank(11, "O'quv ishlari va magistratura bo'lim boshlig'i", LocalDateTime.now()),
                new Rank(12, "Axborot-resurs markazi", LocalDateTime.now()),
                new Rank(13, "Psixologiya va xorijiy tillar fakulteti dekani", LocalDateTime.now()),
                new Rank(14, "Psixologiya kafedrasi mudiri", LocalDateTime.now()),
                new Rank(15, "Psixologiya kafedrasi professor-o'qituvchilari", LocalDateTime.now()),
                new Rank(16, "Filologiya kafedrasi mudiri", LocalDateTime.now()),
                new Rank(17, "Filologiya kafedrasi professor-o'qituvchilari", LocalDateTime.now()),
                new Rank(18, "Pedagogika va umumiy fanlar kafedrasi mudiri", LocalDateTime.now()),
                new Rank(19, "Pedagogika va umumiy fanlar kafedrasi professor-o'qituvchilari", LocalDateTime.now()),
                new Rank(20, "Doktorantlar", LocalDateTime.now()),
                new Rank(21, "1 - kurs Magistrlar", LocalDateTime.now()),
                new Rank(22, "2 - kurs Magistrlar", LocalDateTime.now()),
                new Rank(23, "1 - kurs Bakalavr (kunduzgi)", LocalDateTime.now()),
                new Rank(24, "2 - kurs Bakalavr (kunduzgi)", LocalDateTime.now()),
                new Rank(25, "3 - kurs Bakalavr (kunduzgi)", LocalDateTime.now()),
                new Rank(26, "4 - kurs Bakalavr (kunduzgi)", LocalDateTime.now()),
                new Rank(27, "1 - kurs Bakalavr (Sirtqi)", LocalDateTime.now()),
                new Rank(28, "2 - kurs Bakalavr (Sirtqi)", LocalDateTime.now()),
                new Rank(29, "3 - kurs Bakalavr (Sirtqi)", LocalDateTime.now()),
                new Rank(30, "4 - kurs Bakalavr (Sirtqi)", LocalDateTime.now())
        );

        List<Rank> uniqueRanks = new ArrayList<>();
        for (Rank rank : rankList) {
            if (rankRepo.findByName(rank.getName()).isEmpty()) {
                uniqueRanks.add(rank);
            }
        }

        rankRepo.saveAll(uniqueRanks);


        List<Rank> rectorRanks = rankRepo.findAllById(List.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30));
        Commander rector = new Commander(rankRepo.findById(1).orElseThrow(), rectorRanks, LocalDateTime.now());
        commanderRepo.save(rector);

        List<Rank> managerRanks = rankRepo.findAllById(List.of(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19));
        Commander manager = new Commander(rankRepo.findById(2).orElseThrow(), managerRanks, LocalDateTime.now());
        commanderRepo.save(manager);

        List<Rank> accountantRanks = rankRepo.findAllById(List.of(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30));
        Commander accountant = new Commander(rankRepo.findById(3).orElseThrow(), accountantRanks, LocalDateTime.now());
        commanderRepo.save(accountant);

        List<Rank> registratorRanks = rankRepo.findAllById(List.of( 10, 11, 13, 14, 15, 16, 17, 18, 19,20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30));
        Commander registrat = new Commander(rankRepo.findById(4).orElseThrow(), registratorRanks, LocalDateTime.now());
        commanderRepo.save(registrat);

        List<Rank> dekan1List = rankRepo.findAllById(List.of(  14, 15, 16, 17, 18, 19,  23, 24, 25, 26));
        Commander dekan1 = new Commander(rankRepo.findById(13).orElseThrow(), dekan1List, LocalDateTime.now());
        commanderRepo.save(dekan1);


        List<Rank> sirtqiList = rankRepo.findAllById(List.of(  14, 15, 16, 17, 18, 19,  27, 28, 29, 30));
        Commander sirtqi = new Commander(rankRepo.findById(10).orElseThrow(), sirtqiList, LocalDateTime.now());
        commanderRepo.save(sirtqi);



        List<Rank> magstrList = rankRepo.findAllById(List.of(  14, 15, 16, 17, 18, 19,  21, 22));
        Commander magstr = new Commander(rankRepo.findById(11).orElseThrow(), magstrList, LocalDateTime.now());
        commanderRepo.save(magstr);


        List<Rank> mudr1List = rankRepo.findAllById(List.of(   15, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30));
        Commander mudr1 = new Commander(rankRepo.findById(14).orElseThrow(), mudr1List, LocalDateTime.now());
        commanderRepo.save(mudr1);


        List<Rank> mudr2List = rankRepo.findAllById(List.of(   17, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30));
        Commander mudr2 = new Commander(rankRepo.findById(16).orElseThrow(), mudr2List, LocalDateTime.now());
        commanderRepo.save(mudr2);


        List<Rank> mudr3List = rankRepo.findAllById(List.of(   19, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30));
        Commander mudr3 = new Commander(rankRepo.findById(18).orElseThrow(), mudr3List, LocalDateTime.now());
        commanderRepo.save(mudr3);
    }








    private void saveFaculty() {
        facultyRepo.saveAll(List.of(
                new Faculty(1, "Psixologiya kafedrasi"),
                new Faculty(2, "Filologiya kafedrasi"),
                new Faculty(3, "Pedagogika va umumiy fanlar kafedrasi")
        ));
    }

    private void saveUser(String adminPhone, Optional<User> userByPhone) {
        if (userByPhone.isEmpty()) {
            User user = User.builder()
                    .phone(adminPhone)
                    .password(passwordEncoder.encode("00000000"))
                    .roles(List.of(roleRepo.findByName(UserRoles.ROLE_ADMIN)))
                    .build();
            userRepo.save(user);
            User user1 = User.builder()
                    .phone(adminPhone + "5")
                    .password(passwordEncoder.encode("00000000"))
                    .roles(List.of(roleRepo.findByName(UserRoles.ROLE_ADMIN)))
                    .build();
            userRepo.save(user1);

            User generator = User.builder()
                    .phone("generator")
                    .password(passwordEncoder.encode("00000000"))
                    .roles(List.of(roleRepo.findByName(UserRoles.ROLE_GENERATOR)))
                    .build();
            userRepo.save(generator);
        }
    }

    private void saveRoles() {
        roleRepo.saveAll(List.of(
                new Role(1, UserRoles.ROLE_ADMIN),
                new Role(2, UserRoles.ROLE_STUDENT),
                new Role(3, UserRoles.ROLE_COMPANY),
                new Role(4, UserRoles.ROLE_USER),
                new Role(5, UserRoles.ROLE_TEACHER),
                new Role(6, UserRoles.ROLE_ADMINISTRATOR),
                new Role(7, UserRoles.ROLE_RECTOR),
                new Role(8, UserRoles.ROLE_GENERATOR)
        ));
    }
}
