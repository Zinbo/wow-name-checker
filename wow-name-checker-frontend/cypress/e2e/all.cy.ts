// user journeys:
// can search for a character that does not exist and subscribe to get updates
// can search for a character that does not exist and subscribe, but there's an error so an error box is shown
// can search for a character that does exist and go back to the home screen
// can unsubscribe
// show error when unsubscribe not working
// show 404 page

describe('WoW Name Checker', () => {
    beforeEach(() => {
        cy.visit('http://localhost:3000/')
    })

    it('shows available box when searched character does not exist', () => {
        cy.intercept('GET', '/profile?name=Zinbo&region=Europe&realm=Argent%20Dawn', {statusCode: 404})

        cy.get('input[id="name"]').type("Zinbo");
        cy.get('#server').type("Argent Dawn");
        cy.get('li[data-option-index="1"]').click();
        cy.get('#submit').click();

        cy.location('pathname', {timeout: 60000})
            .should('include', '/check-name');

        cy.get('h1').contains("Zinbo is available on Argent Dawn (Europe)!");
        cy.get('h2').contains("Go get it quick!");
    })

    it('shows taken box when searched character already exists', () => {
        cy.intercept('GET', '/profile?name=Zinbaan&region=Europe&realm=Argent%20Dawn', {statusCode: 200})

        cy.get('input[id="name"]').type("Zinbaan");
        cy.get('#server').type("Argent Dawn");
        cy.get('li[data-option-index="1"]').click();
        cy.get('#submit').click();

        cy.location('pathname', {timeout: 60000})
            .should('include', '/check-name');

        cy.get('h1').contains("Zinbaan is taken on Argent Dawn (Europe) 😔");
        cy.get('h2').contains("Do you want to get an email when it becomes available?");
    })

    it('shows error dialog when API returns a bad response and the user is navigated back to home page', () => {
        cy.intercept('GET', '/profile?name=Bad&region=Europe&realm=Argent%20Dawn', {statusCode: 500})
        cy.get('input[id="name"]').type("Bad");
        cy.get('#server').type("Argent Dawn");
        cy.get('li[data-option-index="1"]').click();
        cy.get('#submit').click();

        cy.location('pathname', {timeout: 60000})
            .should('include', '/check-name');

        cy.get('#alert-dialog-title').contains("Could not load WoW Character");
        cy.get('#alert-dialog-description').contains("Please try again later.");
    })

    it('allows the user to subscribe if a character name is taken', () => {
        const email = "my@email.com";
        const character = "Zinbaan";
        const realm = "Argent Dawn";
        const region = "Europe";
        const alias = 'request';
        cy.intercept('POST', '/alert', (req) => {
            if(req.body?.email === email && req.body?.character === character
                && req.body?.realm === realm && req.body?.region === region) req.alias = alias;
            req.continue(res => {
                res.statusCode = 200
            })
        })

        cy.get('input[id="name"]').type(character);
        cy.get('#server').type(realm);
        cy.get('li[data-option-index="1"]').click();
        cy.get('#submit').click();

        cy.location('pathname', {timeout: 60000})
            .should('include', '/check-name');

        cy.get('#email').type(email);

        cy.get('#submit').click();

        cy.wait(`@${alias}`)

        cy.get('#successText').contains("Subscribed successfully!")
    })
})

export {}
