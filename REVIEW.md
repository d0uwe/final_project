## Peer review feedback:
- sign in listener in MainActivity is anonymous
- - fixed
- comments boven functies zijn met // ipv /**
- - done consistently, stays unchanged
- code grouping binnen highscoresActivity is niet aanwezig
- - fixed
- loggedInActivity: alle onclicklisteners worden in de code gezet maar is niet dynamisch. Kan beter in xml
- - not fixed, i see no way around this when the listeners are a private class
- witregels in loggedInActivity zouden de leesbaarheid hier en daar wat verhogen
- - fixed
- in questionActivity: kan de database final zijn? Vgm kan die veranderen
- - not fixed, since it works
- bij parseResponse in questionActivity, misschien kun je uitleggen waarom er ineens "what " komt te staan?
- - fixed
- onComplete in OnDoneCreatingUser in registerActivity is wat lang, misschien kan dit opgedeeld worden in functies.
- - not fixed, function is 30 lines. Styleguide:  If a method exceeds 40 lines or so, think about whether it can be broken up without harming the structure of the program.
- WITREGELS :OnDoneCreatingUser
- - fixed
