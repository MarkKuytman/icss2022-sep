# Eisen en uitbreidingen

In dit document is opgenomen aan welke eisen het product voldoet en welke uitbreidingen zijn gedaan.

## Aan welke eisen voldoet het product

Het product voldoet aan alle MUST en SHOULD eisen uit [ASSIGNMENT.md](ASSIGNMENT.md).

## Welke uitbreidingen zijn gedaan

In tabel 1 is een overzicht welke uitbreidingen zijn gedaan en hoeveel punten die mogelijk opleveren. Bij de meeste uitbreidingen is onder de tabel een voorbeeld gegeven ter verduidelijking.

_Tabel 1 - Uitbreidingen (status van toevoeging)._

| #   | Omschrijving                                                               | Status     |
| --- | -------------------------------------------------------------------------- | ---------- |
| 1   | Delen is ingebracht.                                                       | Toegevoegd |
| 2   | Checken of er geen dubbele declaraties voorkomen tijdens de transformatie. | Toegevoegd |

Er is ook een nieuwe test, `level4.icss`, erbij gemaakt voor het testen van de parser op nested variable assignments die op hetzelfde niveau worden gebruikt voor controle op een if clause.

## Voorbeelden van uitbreidingen

- **Delen is ingebracht:**

  Voorbeeld ICSS:

  ```ICSS
  .container {
      width: 100px / 2;
  }
  ```

  Resultaat (verwacht): de deling wordt geëvalueerd tijdens transformatie en `width: 50px` geproduceerd.

- **Check op dubbele declaraties tijdens transformatie:**

  Voorbeeld ICSS waarin duplicate voorkomt:

  ```ICSS
  .text {
    color: #111;
    color: #222; /* dubbele declaratie */
    if (white) {
      color: #ffffff; /* nested dubbele declaratie */
    }
  }
  ```

  Verwachte gedrag: de transformer detecteert dubbele declaraties en handelt volgens beleid (bijv. laatste heeft prioriteit of er wordt een waarschuwing gegeven). In mijn implementatie wordt dubbele declaratie gedetecteerd en expliciet afgehandeld zodat ongewenste duplicatie wordt voorkomen.
